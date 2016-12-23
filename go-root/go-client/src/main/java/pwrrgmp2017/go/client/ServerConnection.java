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

/**
 * Observable object which is the main form of communication between us and the
 * server. It connects to the server and lets other classes to receive and send
 * messages from/to the server. It can also be run as a thread after successful
 * connection, so it will be constantly listening for messages from the server
 * and notify observers by events in the form of {@link ProtocolMessage} or
 * {@link IOException} in case of an error in communication.
 */
public class ServerConnection extends Observable implements Runnable
{
	/**
	 * Reference to logger.
	 */
	protected static final Logger LOGGER = Logger.getLogger(ClientMain.class.getName());

	/**
	 * The currently running thread, null if the thread has not been started.
	 */
	protected Thread thread;

	/**
	 * Address of the server to connect to.
	 */
	protected String hostname;

	/**
	 * Port of the server to connect to.
	 */
	protected int port;

	/**
	 * This class is implemented using sockets.
	 */
	protected Socket socket;

	/**
	 * Input from the server.
	 */
	protected BufferedReader input;

	/**
	 * Output to the server.
	 */
	protected PrintWriter output;

	/**
	 * Validates the parameters.
	 * 
	 * @param hostname
	 *            address of the server to connect to
	 * @param port
	 *            port of the server to connect to
	 * @throws InvalidParameterException
	 *             when the port is wrong
	 */
	public ServerConnection(String hostname, String port) throws InvalidParameterException
	{
		validateAndSetData(hostname, port);
	}

	/**
	 * Less safe constructor, as it doesn't validate the parameters. Useful if
	 * you validate the data yourself.
	 * 
	 * @param hostname
	 *            address of the server
	 * @param port
	 *            port of the server
	 */
	public ServerConnection(String hostname, int port)
	{
		this.hostname = hostname;
		this.port = port;
	}

	/**
	 * Access to the currently running thread.
	 * 
	 * @return thread if it has been started, null otherwise
	 * @see #startReceiving()
	 */
	public Thread getThread()
	{
		return thread;
	}

	/**
	 * Starts the thread which constantly listens for messages from the server
	 * and notifies Observers about events in the form of
	 * {@link ProtocolMessage} or {@link IOException} in case of an error in
	 * communication.
	 */
	public void startReceiving()
	{
		LOGGER.info("Starting the server connection thread.");
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Reads a message (one line string) from the server.
	 * <p>
	 * Should NOT be used after using {@link #startReceiving()}, as using this
	 * function simultaneously with the thread may result in undefined behaviour
	 * and there is no reliable way to end the thread without closing the
	 * connection. Receiving should be done by adding yourself to the observers
	 * list.
	 * 
	 * @return message from the server or null if the connection was closed on
	 *         the other end
	 * @throws IOException
	 *             if there was a problem with communication
	 */
	public synchronized String receive() throws IOException
	{
		return input.readLine();
	}

	/**
	 * Sends the message to the server.
	 * 
	 * @param message
	 *            message to send
	 */
	public void send(String message)
	{
		output.println(message);
	}

	/**
	 * Closes the connection with the server. It also stops the thread without
	 * notifying other observers.
	 */
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

	/**
	 * Checks if the connection was closed.
	 * 
	 * @return true if the connection was closed
	 */
	public boolean isClosed()
	{
		return socket.isClosed();
	}

	/**
	 * Thread function. Should not be called directly, instead use {@link #startReceiving()}.
	 */
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
				LOGGER.info(isClosed() + "");
				if (!isClosed())
				{
					// Error on server side
					setChanged();
					this.notifyObservers(e);
					LOGGER.info("IO happened");
				} // else the client itself closed the connection, no need to
					// notify
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

	/**
	 * Connects to the server.
	 * 
	 * @throws IOException
	 *             when there was a problem with connection
	 */
	public void connect() throws IOException
	{
		LOGGER.log(Level.INFO, "Server connection is starting.");
		createSocket();
		createInputOutput();
		LOGGER.log(Level.INFO, "Server connection is ok.");
	}

	/**
	 * Used in constructor to validate parameters and set appropriate fields.
	 * 
	 * @param hostname
	 *            address of the server
	 * @param port
	 *            port of the server
	 * @throws InvalidParameterException
	 *             when the parameters are wrong
	 */
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

	/**
	 * Creates a socket and sets the appropriate field.
	 * 
	 * @throws IOException
	 *             if something went wrong during connection
	 */
	protected void createSocket() throws IOException
	{
		this.socket = new Socket(hostname, port);
	}

	/**
	 * Creates a reader and writer from/to the server and sets the appropriate
	 * fields. Is called after creating a socket.
	 * 
	 * @throws IOException if streams could not be get from the socket
	 */
	protected void createInputOutput() throws IOException
	{
		this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.output = new PrintWriter(socket.getOutputStream(), true);
	}
}
