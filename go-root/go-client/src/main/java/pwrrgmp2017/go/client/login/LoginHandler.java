package pwrrgmp2017.go.client.login;

import java.io.IOException;
import java.util.Observable;

import pwrrgmp2017.go.client.ServerConnection;
import pwrrgmp2017.go.clientserverprotocol.LoginProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.LoginResponseProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ProtocolMessage;

/**
 * Handles the login process in a thread using already created server connection
 * and notifies the observers whether it was successful or not. The threads ends
 * either way after that and closes the connection on failure.
 */
public class LoginHandler extends Observable implements Runnable
{
	/**
	 * The name of the player.
	 */
	private final String playerName;

	/**
	 * Server connection (already connected).
	 */
	private final ServerConnection serverConnection;

	/**
	 * Flag for observers if the player successfully logged in the server.
	 */
	private boolean isLoginSuccessful;

	/**
	 * Information for observers with a reason for login failure.
	 */
	private String reason;

	/**
	 * Constructor. It does not start the thread.
	 * 
	 * @param connection
	 *            already established connection to the server
	 * @param playerName
	 */
	public LoginHandler(ServerConnection connection, String playerName)
	{
		this.playerName = playerName;
		this.serverConnection = connection;
		this.isLoginSuccessful = false;
		this.reason = "Thread not done yet.";
	}
	
	/**
	 * Starts the thread.
	 */
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
	
	/**
	 * Information for observers. If the login was not successful, see {@link #getReason()}.
	 * @return true if the login was successful
	 */
	public boolean getIsLoginSuccessful()
	{
		return isLoginSuccessful;
	}
	
	/**
	 * Information for observers.
	 * @return string containing an information why the login was/was not successful
	 */
	public String getReason()
	{
		return reason;
	}

	/**
	 * @return the server connection used by the handler
	 */
	public ServerConnection getServerConnection()
	{
		return serverConnection;
	}
}
