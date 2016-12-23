package pwrrgmp2017.go.server.connection;

import java.io.IOException;
import java.util.logging.Logger;

import pwrrgmp2017.go.clientserverprotocol.LoginProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.LoginResponseProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ProtocolMessage;
import pwrrgmp2017.go.server.GamesManager;
import pwrrgmp2017.go.server.Exceptions.SameNameException;

/**
 * Thread which handles the login process of the player.
 */
public class LogPlayerHandler implements Runnable
{
	/**
	 * Reference to logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(LogPlayerHandler.class.getName());

	/**
	 * Player connection (already established).
	 */
	private final RealPlayerConnection connection;
	
	/**
	 * Reference to the games manager.
	 */
	private final GamesManager gamesManager;

	/**
	 * Constructor.
	 * @param connection real player connection (already established)
	 * @param gamesManager reference to the games manager
	 */
	public LogPlayerHandler(RealPlayerConnection connection, GamesManager gamesManager)
	{
		this.connection = connection;
		this.gamesManager = gamesManager;
	}

	/**
	 * The thread.
	 */
	@Override
	public void run()
	{
		String message;
		try
		{
			message = connection.receive();
		}
		catch (IOException e)
		{
			LOGGER.warning("Could not receive message from client: " + e.getMessage());
			connection.close();
			return;
		}

		if (message == null)
		{
			LOGGER.warning("Client unexpectedly close the connection.");
			connection.close();
			return;
		}

		ProtocolMessage genericMessage = ProtocolMessage.getProtocolMessage(message);
		LoginResponseProtocolMessage response;
		if (genericMessage instanceof LoginProtocolMessage)
		{
			LoginProtocolMessage loginProtocolMessage = (LoginProtocolMessage) genericMessage;
			try
			{
				gamesManager.addChoosingPlayer(connection, loginProtocolMessage.getUsername());
				LOGGER.info("Login successful for " + loginProtocolMessage.getUsername());
				response = new LoginResponseProtocolMessage(true, "Successfully logged in.");
				connection.send(response.getFullMessage());
				LOGGER.info("Sent to " + loginProtocolMessage.getUsername());
				return;
			}
			catch (SameNameException e)
			{
				response = new LoginResponseProtocolMessage(false, "The name is already taken.");
				connection.send(response.getFullMessage());
				connection.close();
				return;
			}
		}
		else
		{
			LOGGER.warning("Got wrong message from client: " + genericMessage.getFullMessage());
			response = new LoginResponseProtocolMessage(false, "Wrong message received.");
			connection.send(response.getFullMessage());
			connection.close();
			return;
		}
	}

}
