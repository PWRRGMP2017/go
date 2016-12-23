package pwrrgmp2017.go.server.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

import pwrrgmp2017.go.server.ServerMain;

/**
 * Represents a connection to the real player.
 */
public class RealPlayerConnection extends PlayerConnection
{
	/**
	 * Reference to logger.
	 */
	protected static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());

	/**
	 * Open socket.
	 */
	protected Socket socket;
	
	/**
	 * Input from the player.
	 */
	protected BufferedReader input;
	
	/**
	 * Output to the player.
	 */
	protected PrintWriter output;
	
	/**
	 * Constructor.
	 * @param socket open socket
	 * @throws IOException if there was a problem with I/O socket streams
	 */
	public RealPlayerConnection(Socket socket) throws IOException
	{
		this.socket = socket;
		this.playerInfo = new PlayerInfo("");
		createInputOutput();
	}
	
	/**
	 * For constructor, sets {@link #input} and {@link #output} fields.
	 * @throws IOException if there was a problem with I/O socket streams
	 */
	protected void createInputOutput() throws IOException
	{
		this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.output = new PrintWriter(socket.getOutputStream(), true);
	}
	
	@Override
	public String receive() throws IOException
	{
		return input.readLine();
	}

	@Override
	public synchronized void send(String message)
	{
		output.println(message);
	}

	@Override
	public void close()
	{
		if (socket.isClosed())
		{
			LOGGER.warning("Client connection is already closed.");
			return;
		}

		LOGGER.info("Ending client connection.");
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
	 * @return true if the socket was closed
	 */
	public boolean isClosed()
	{
		return socket.isClosed();
	}

}
