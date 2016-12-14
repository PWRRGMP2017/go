package pwrrgmp2017.go.server.connection;

import java.io.IOException;
import java.util.logging.Logger;

import pwrrgmp2017.go.clientserverprotocol.LoginProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.LoginResponseProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ProtocolMessage;
import pwrrgmp2017.go.server.GamesManager;
import pwrrgmp2017.go.server.Exceptions.SameNameException;

public class LogPlayerHandler implements Runnable
{
	private static final Logger LOGGER = Logger.getLogger(LogPlayerHandler.class.getName());

	private final RealPlayerConnection connection;
	private final GamesManager gamesManager;

	public LogPlayerHandler(RealPlayerConnection connection, GamesManager gamesManager)
	{
		this.connection = connection;
		this.gamesManager = gamesManager;
	}

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
				response = new LoginResponseProtocolMessage(true, "Successfully logged in.");
				connection.send(response.getFullMessage());
				LOGGER.info("Login successful for " + loginProtocolMessage.getUsername());
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
