package pwrrgmp2017.go.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidParameterException;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import pwrrgmp2017.go.clientserverprotocol.ProtocolMessage;

public class ServerConnection extends Observable implements Runnable
{
	protected static final Logger LOGGER = Logger.getLogger(ClientMain.class.getName());

	protected Thread thread;

	protected String hostname;
	protected int port;

	protected Socket socket;
	protected BufferedReader input;
	protected PrintWriter output;

	public ServerConnection(String hostname, String port) throws InvalidParameterException, IOException
	{
		validateAndSetData(hostname, port);
		connect();
	}

	public ServerConnection(String hostname, int port) throws IOException
	{
		this.hostname = hostname;
		this.port = port;
		connect();
	}

	public Thread getThread()
	{
		return thread;
	}

	public void startReceiving()
	{
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	protected void connect() throws IOException
	{
		LOGGER.log(Level.INFO, "Server connection is starting.");
		createSocket();
		createInputOutput();
		LOGGER.log(Level.INFO, "Server connection is ok.");
	}

	protected void validateAndSetData(String hostname, String port) throws InvalidParameterException
	{
		if (hostname.isEmpty() || port.isEmpty())
		{
			throw new InvalidParameterException("Empty fields!");
		}

		this.hostname = hostname;

		try
		{
			this.port = Integer.parseInt(port);
		}
		catch (NumberFormatException e)
		{
			throw new InvalidParameterException("Port must be a number!");
		}

		if (this.port < 1024)
		{
			throw new InvalidParameterException("Port must be greater than 1024!");
		}
	}

	public String receive() throws IOException
	{
		return input.readLine();
	}

	public void send(String message)
	{
		output.println(message);
	}

	public void close()
	{
		if (socket.isClosed())
		{
			LOGGER.warning("Server connection is already closed.");
			return;
		}

		LOGGER.info("Ending server connection.");
		try
		{
			socket.close();
		}
		catch (IOException e)
		{
			LOGGER.warning("Could not close socket: " + e.getMessage());
		}
	}

	public boolean isClosed()
	{
		return socket.isClosed();
	}

	protected void createSocket() throws IOException
	{
		this.socket = new Socket(hostname, port);
	}

	protected void createInputOutput() throws IOException
	{
		this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.output = new PrintWriter(socket.getOutputStream(), true);
	}

	@Override
	public void run()
	{
		while (!Thread.interrupted())
		{
			// Wait for a message
			String message = null;
			try
			{
				message = receive();
			}
			catch (IOException e)
			{
				if (!socket.isClosed())
				{
					// Error on server side
					setChanged();
					this.notifyObservers(e);
				} // else the client itself closed the connection, no need to notify
				return;
			}

			if (message == null)
			{
				close();
				setChanged();
				this.notifyObservers(new IOException("Server unexpectedly closed the connection."));
				return;
			}

			// Notify observers
			ProtocolMessage genericMessage = ProtocolMessage.getProtocolMessage(message);
			LOGGER.info("Received message: " + genericMessage.getFullMessage());
			setChanged();
			notifyObservers((Object) genericMessage);
		}
	}
}
