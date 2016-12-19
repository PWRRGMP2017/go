package pwrrgmp2017.go.server.connection;

import java.io.IOException;
import java.util.logging.Logger;

import pwrrgmp2017.go.clientserverprotocol.ConfirmationProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ExitProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ResignProtocolMessage;
import pwrrgmp2017.go.server.GamesManager;

public class PlayingPlayerHandler implements Runnable
{
	private static final Logger LOGGER = Logger.getLogger(NotYetPlayingPlayerHandler.class.getName());

	private final RealPlayerConnection connection;
	private final GamesManager gamesManager;

	private final PlayerConnection opponent;

	public PlayingPlayerHandler(RealPlayerConnection player, GamesManager gamesManager)
	{
		this.connection = player;
		this.gamesManager = gamesManager;
		this.opponent = player.getPlayerInfo().getPlayingGame().getOpponent(connection);
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
			else if (genericMessage instanceof ResignProtocolMessage)
			{
				LOGGER.info("Player " + connection.getPlayerName() + " resigned.");
				ResignProtocolMessage resignation = (ResignProtocolMessage) genericMessage;
				opponent.send(resignation.getFullMessage());
				gamesManager.deleteGame(connection.getPlayerInfo().getPlayingGame());
				return;
			}
			else if (genericMessage instanceof ConfirmationProtocolMessage)
			{
				LOGGER.info("Player " + connection.getPlayerName() + " confirmed resignation.");
				return;
			}
			// move, pass, resign etc. just send the same message to opponent
			// and are done in the server's game model
			else
			{
				LOGGER.warning("Got wrong message from client: " + genericMessage.getFullMessage());
			}
		}

		cleanUp();
	}

	private void cleanUp()
	{
		connection.close();

		// Send the other player message
		ResignProtocolMessage message = new ResignProtocolMessage("Player closed the connection.");
		opponent.send(message.getFullMessage());
		// The client should disable the controls and show the results

		// Let games manager clean up
		gamesManager.deleteGame(connection.getPlayerInfo().getPlayingGame());
	}

}
