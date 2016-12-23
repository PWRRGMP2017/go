package pwrrgmp2017.go.server.connection;

import java.io.IOException;
import java.util.logging.Logger;

import pwrrgmp2017.go.clientserverprotocol.CancelWaitingProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.CancelWaitingResponseProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ConfirmationProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ExitProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.InvitationProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.InvitationResponseProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.PlayBotGameProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.PlayerFoundProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.WaitForGameProtocolMessage;
import pwrrgmp2017.go.game.factory.GameInfo;
import pwrrgmp2017.go.server.GamesManager;
import pwrrgmp2017.go.server.Exceptions.BadPlayerException;
import pwrrgmp2017.go.server.Exceptions.LostPlayerConnection;
import pwrrgmp2017.go.server.Exceptions.tooLateToBackPlayerException;

/**
 * Thread for not yet playing player. Reacts to player messages regarding
 * invitations, searching for player and starting a game.
 */
public class NotYetPlayingPlayerHandler implements Runnable
{
	/**
	 * Reference to logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(NotYetPlayingPlayerHandler.class.getName());

	/**
	 * Connection to the player.
	 */
	private final RealPlayerConnection connection;

	/**
	 * Reference to games manager.
	 */
	private final GamesManager gamesManager;

	/**
	 * Invitation sent from/to us.
	 */
	private InvitationProtocolMessage invitation;

	/**
	 * The game our player is waiting for.
	 */
	private GameInfo waitingGame;

	/**
	 * Constructor.
	 * 
	 * @param player
	 *            player connection
	 * @param gamesManager
	 *            reference to the games manager
	 */
	public NotYetPlayingPlayerHandler(RealPlayerConnection player, GamesManager gamesManager)
	{
		this.connection = player;
		this.gamesManager = gamesManager;
		this.invitation = null;
		this.waitingGame = null;
	}

	/**
	 * The thread.
	 */
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
			else if (genericMessage instanceof PlayBotGameProtocolMessage)
			{
				LOGGER.info("Player " + connection.getPlayerName() + " wants to play a game with bot.");
				try
				{
					gamesManager.playBotGame(connection, ((PlayBotGameProtocolMessage) genericMessage).getGameInfo());
				}
				catch (BadPlayerException e)
				{
					// Should not happen
					e.printStackTrace();
				}

				return;
			}
			else if (genericMessage instanceof WaitForGameProtocolMessage)
			{
				LOGGER.info("Player " + connection.getPlayerName() + " is searching for player.");
				waitingGame = ((WaitForGameProtocolMessage) genericMessage).getGameInfo();
				PlayerConnection secondPlayer;
				secondPlayer = gamesManager.waitForGame(connection, waitingGame);

				if (secondPlayer != null)
				{
					connection
							.send(new PlayerFoundProtocolMessage(secondPlayer.getPlayerName(), true).getFullMessage());
					secondPlayer
							.send(new PlayerFoundProtocolMessage(connection.getPlayerName(), false).getFullMessage());
					try
					{
						gamesManager.createGame(connection, secondPlayer, waitingGame);
					}
					catch (BadPlayerException e)
					{
						// Should. Not. Happen.
						e.printStackTrace();
					}
					return;
				}
			}
			else if (genericMessage instanceof CancelWaitingProtocolMessage)
			{
				LOGGER.info("Player " + connection.getPlayerName() + " wants to cancel waiting.");
				CancelWaitingResponseProtocolMessage response;
				try
				{
					gamesManager.stopWaiting(connection, waitingGame.getAsString());
					waitingGame = null;
					response = new CancelWaitingResponseProtocolMessage(true);
					connection.send(response.getFullMessage());
				}
				catch (tooLateToBackPlayerException e)
				{
					LOGGER.warning("Player " + connection.getPlayerName() + " cannot cancel waiting now.");
					response = new CancelWaitingResponseProtocolMessage(false);
					connection.send(response.getFullMessage());
				}
			}
			else if (genericMessage instanceof ConfirmationProtocolMessage)
			{
				LOGGER.info("Player " + connection.getPlayerName() + " confirms he's playing.");
				return;
			}
			else
			{
				LOGGER.warning("Got wrong message from client: " + genericMessage.getFullMessage());
			}
		}

		cleanUp();
	}

	/**
	 * Handles invitation response if the player is inviting.
	 * 
	 * @return true if the thread should exit
	 */
	private boolean handleInvitingResponse(InvitationResponseProtocolMessage receivedMessage)
	{
		if (receivedMessage.getIsAccepted())
		{
			// The invited player accepted the invitation

			// Tell him we are playing
			connection.getInvitedPlayer().send(receivedMessage.getFullMessage());

			// Tell game manager to create a game
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

	/**
	 * Handles the invitation response if the player is invited.
	 * 
	 * @return if the thread should exit
	 */
	private boolean handleInvitedResponse(InvitationResponseProtocolMessage receivedMessage)
	{
		// Send forward the message to the inviting player
		connection.getInvitingPlayer().send(receivedMessage.getFullMessage());
		if (receivedMessage.getIsAccepted())
		{
			// End the thread and wait for the game to start
			return true;
		}
		else
		{
			// We are not playing
			connection.getInvitingPlayer().cancelInvitation();
			connection.cancelInvitation();
			return false;
		}
	}
	
	/**
	 * Closes the connection and cleans up after an error.
	 */
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
	
	/**
	 * Handle invitation message.
	 */
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
