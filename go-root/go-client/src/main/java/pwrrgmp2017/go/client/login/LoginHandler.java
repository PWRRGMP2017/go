package pwrrgmp2017.go.client.login;

import java.io.IOException;
import java.util.Observable;

import pwrrgmp2017.go.client.ServerConnection;
import pwrrgmp2017.go.clientserverprotocol.LoginProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.LoginResponseProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ProtocolMessage;

/**
 * Handles the login process in a thread using already created server
 * connection.
 */
public class LoginHandler extends Observable implements Runnable
{
	private final String playerName;
	private final ServerConnection serverConnection;

	private boolean isLoginSuccessful;
	private String reason;

	public LoginHandler(ServerConnection connection, String playerName)
	{
		this.playerName = playerName;
		this.serverConnection = connection;
		this.isLoginSuccessful = false;
		this.reason = "Thread not done yet.";
	}

	@Override
	public void run()
	{
		// Send login message to server
		LoginProtocolMessage message = new LoginProtocolMessage(playerName);
		serverConnection.send(message.getFullMessage());

		// Try to get a response
		String response = null;
		try
		{
			response = serverConnection.receive();
		}
		catch (IOException e)
		{
			this.reason = e.getMessage();
			this.isLoginSuccessful = false;
			this.setChanged();
			this.notifyObservers();
			return;
		}

		if (response == null)
		{
			serverConnection.close();
			this.reason = "Server unexpectedly closed the connection.";
			this.isLoginSuccessful = false;
			this.setChanged();
			this.notifyObservers();
			return;
		}

		// Interpret the response
		ProtocolMessage genericMessage = ProtocolMessage.getProtocolMessage(response);
		if (genericMessage instanceof LoginResponseProtocolMessage)
		{
			LoginResponseProtocolMessage loginResponse = (LoginResponseProtocolMessage) genericMessage;
			this.reason = loginResponse.getReason();
			this.isLoginSuccessful = loginResponse.getIsAccepted();
			if (!loginResponse.getIsAccepted())
			{
				serverConnection.close();
			}
			this.setChanged();
			this.notifyObservers();
			return;
		}
		else
		{
			this.reason = "Got wrong message: " + genericMessage.getFullMessage();
			this.isLoginSuccessful = false;
			this.setChanged();
			this.notifyObservers();
			return;
		}
	}

	public boolean getIsLoginSuccessful()
	{
		return isLoginSuccessful;
	}

	public String getReason()
	{
		return reason;
	}

	public ServerConnection getServerConnection()
	{
		return serverConnection;
	}
}
