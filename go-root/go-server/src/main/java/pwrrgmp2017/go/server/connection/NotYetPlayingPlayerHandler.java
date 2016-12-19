package pwrrgmp2017.go.server.connection;

import java.io.IOException;
import java.util.logging.Logger;

import pwrrgmp2017.go.clientserverprotocol.ExitProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.InvitationProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.InvitationResponseProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ProtocolMessage;
import pwrrgmp2017.go.server.GamesManager;
import pwrrgmp2017.go.server.Exceptions.BadPlayerException;
import pwrrgmp2017.go.server.Exceptions.LostPlayerConnection;

public class NotYetPlayingPlayerHandler implements Runnable
{
	private static final Logger LOGGER = Logger.getLogger(NotYetPlayingPlayerHandler.class.getName());

	private final RealPlayerConnection connection;
	private final GamesManager gamesManager;

	private InvitationProtocolMessage invitation;

	public NotYetPlayingPlayerHandler(RealPlayerConnection player, GamesManager gamesManager)
	{
		this.connection = player;
		this.gamesManager = gamesManager;
		this.invitation = null;
	}

	@Override
	public void run()
	{
		String message;
		while (!Thread.currentThread().isInterrupted())
		{
			// Receive the message
			try
			{
				message = connection.receive();
			}
			catch (IOException e)
			{
				LOGGER.warning(
						"Could not receive message from client " + connection.getPlayerName() + ": " + e.getMessage());
				cleanUp();
				return;
			}

			if (message == null)
			{
				LOGGER.warning("Client " + connection.getPlayerName() + " unexpectedly closed the connection.");
				cleanUp();
				return;
			}

			// Interpret the message
			ProtocolMessage genericMessage = ProtocolMessage.getProtocolMessage(message);

			if (genericMessage instanceof ExitProtocolMessage)
			{
				LOGGER.info("Player " + connection.getPlayerName() + " wants to exit.");
				cleanUp();
				return;
			}
			else if (genericMessage instanceof InvitationProtocolMessage)
			{
				LOGGER.info("Player " + connection.getPlayerName() + " wants to invite somebody.");
				this.invitation = (InvitationProtocolMessage) genericMessage;
				handleInvitation();
			}
			else if (genericMessage instanceof InvitationResponseProtocolMessage)
			{
				LOGGER.info("Player " + connection.getPlayerName() + " sent invitation response.");
				InvitationResponseProtocolMessage receivedMessage = (InvitationResponseProtocolMessage) genericMessage;
				if (connection.isInvited())
				{
					if (handleInvitedResponse(receivedMessage))
					{
						return;
					}
				}
				else if (connection.isInviting())
				{
					if (handleInvitingResponse(receivedMessage))
					{
						return;
					}
				}
				else
				{
					LOGGER.warning("Player " + connection.getPlayerName()
							+ " received an invitation response, but is not inviting or invited.");
				}
			}
			else
			{
				LOGGER.warning("Got wrong message from client: " + genericMessage.getFullMessage());
			}
		}

		cleanUp();
	}

	/*
	 * @return if the thread should exit
	 */
	private boolean handleInvitingResponse(InvitationResponseProtocolMessage receivedMessage)
	{
		if (receivedMessage.getIsAccepted())
		{
			// The invited player accepted the invitation
			// Tell game manager to create a game here

			// For now, let's just reset state
			// invitation = null;
			// connection.getInvitedPlayer().cancelInvitation();
			// connection.cancelInvitation();

			try
			{
				gamesManager.createGame(connection, gamesManager.getChoosingPlayer(invitation.getToPlayerName()),
						invitation.getGameInfo());
			}
			catch (BadPlayerException e)
			{
				// Should not happen
				e.printStackTrace();
				return false;
			}

			return true;
		}
		else
		{
			// Our player has cancelled the invitation
			invitation = null;
			connection.getInvitedPlayer().send(receivedMessage.getFullMessage());
			connection.getInvitedPlayer().cancelInvitation();
			connection.cancelInvitation();
			return false;
		}
	}

	/*
	 * @return if the thread should exit
	 */
	private boolean handleInvitedResponse(InvitationResponseProtocolMessage receivedMessage)
	{
		// Just send the message to the inviting player and let him handle the
		// rest
		connection.getInvitingPlayer().send(receivedMessage.getFullMessage());

		// End the thread and wait for the game to start
		return true;
	}

	private void cleanUp()
	{
		connection.close();

		if (connection.isInvited())
		{
			InvitationResponseProtocolMessage response = new InvitationResponseProtocolMessage(false,
					"Player " + connection.getPlayerName() + " unexpectedly closed the connection.");
			connection.getInvitingPlayer().send(response.getFullMessage());
			connection.getInvitingPlayer().cancelInvitation();
			connection.cancelInvitation();
		}

		if (connection.isInviting())
		{
			InvitationResponseProtocolMessage response = new InvitationResponseProtocolMessage(false,
					"Player " + connection.getPlayerName() + " unexpectedly closed the connection.");
			connection.getInvitedPlayer().send(response.getFullMessage());
			connection.getInvitedPlayer().cancelInvitation();
			connection.cancelInvitation();
		}

		try
		{
			gamesManager.deletePlayer(connection);
		}
		catch (LostPlayerConnection e)
		{
			// ?
			e.printStackTrace();
		}
	}

	private void handleInvitation()
	{
		InvitationResponseProtocolMessage response;

		// Should not happen
		if (connection.isInviting())
		{
			LOGGER.warning("Player " + connection.getPlayerName() + " is already inviting.");
			return;
		}

		// Get a second player connection
		PlayerConnection secondPlayer;
		try
		{
			secondPlayer = gamesManager.getChoosingPlayer(invitation.getToPlayerName());
		}
		catch (BadPlayerException e)
		{
			response = new InvitationResponseProtocolMessage(false, e.getMessage());
			connection.send(response.getFullMessage());
			connection.cancelInvitation();
			return;
		}

		if (secondPlayer == connection)
		{
			response = new InvitationResponseProtocolMessage(false, "You can't invite yourself!");
			connection.send(response.getFullMessage());
			connection.cancelInvitation();
			return;
		}

		// Our player is inviting, we don't want anyone to invite him at the
		// moment
		connection.invitePlayer(secondPlayer);

		// Let's try to invite the second player.
		if (!secondPlayer.inviteThisPlayerBy(connection))
		{
			response = new InvitationResponseProtocolMessage(false, "The player is already invited or inviting.");
			connection.send(response.getFullMessage());
			connection.cancelInvitation();
			return;
		}

		// Let's send the invitation
		secondPlayer.send(invitation.getFullMessage());

		LOGGER.fine("The invitation was send to " + secondPlayer.getPlayerName());

		// Everything set up, the client of the second player should receive an
		// invitation
	}

}
