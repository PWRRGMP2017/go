package pwrrgmp2017.go.server.connection;

import java.io.IOException;
import java.util.logging.Logger;

import pwrrgmp2017.go.clientserverprotocol.ExitProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ProtocolMessage;
import pwrrgmp2017.go.server.GamesManager;
import pwrrgmp2017.go.server.Exceptions.LostPlayerConnection;

public class NotYetPlayingPlayerHandler implements Runnable
{
	private static final Logger LOGGER = Logger.getLogger(NotYetPlayingPlayerHandler.class.getName());

	private final RealPlayerConnection connection;
	private final GamesManager gamesManager;

	public NotYetPlayingPlayerHandler(RealPlayerConnection player, GamesManager gamesManager)
	{
		this.connection = player;
		this.gamesManager = gamesManager;
	}

	@Override
	public void run()
	{
		String message;
		while (!Thread.currentThread().isInterrupted())
		{
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
				LOGGER.warning("Client " + connection.getPlayerName() + " unexpectedly close the connection.");
				cleanUp();
				return;
			}

			ProtocolMessage genericMessage = ProtocolMessage.getProtocolMessage(message);

			if (genericMessage instanceof ExitProtocolMessage)
			{
				LOGGER.info("Player " + connection.getPlayerName() + " wants to exit.");
				cleanUp();
				return;
			}
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

}
