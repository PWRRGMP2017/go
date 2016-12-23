package pwrrgmp2017.go.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import pwrrgmp2017.go.server.connection.RealPlayerConnection;

/**
 * The server main thread, which only accepts the connections and lets the {@link GamesManager} care about them.
 */
public class Server extends Thread
{
	/**
	 * Reference to logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());

	/**
	 * Reference to the games manager.
	 */
	GamesManager gamesManager;

	/**
	 * The server port.
	 */
	private int port;
	
	/**
	 * The server socket.
	 */
	private ServerSocket serverSocket;

	/**
	 * Starts the server and server thread.
	 * @param port server port
	 * @throws IllegalArgumentException ig the port is wrong
	 * @throws IOException if the server could not be created
	 */
	Server(int port) throws IllegalArgumentException, IOException
	{
		if (port < 5001 && port != 0)
		{
			LOGGER.log(Level.SEVERE, "Post must be greater than 5000: {0}", port);
			throw new IllegalArgumentException();
		}

		this.port = port;
		this.gamesManager = GamesManager.getInstance();

		LOGGER.log(Level.INFO, "Server is starting.");
		serverSocket = new ServerSocket(port);
		this.port = serverSocket.getLocalPort();

		this.start();
	}
	
	/**
	 * The thread accepting connections. Should not be called directly.
	 */
	@Override
	public void run()
	{
		LOGGER.log(Level.INFO, "Server is running.");
		Socket playerSocket;
		while (!this.isInterrupted())
		{
			try
			{
				playerSocket = serverSocket.accept();
				gamesManager.createPlayerConnection(new RealPlayerConnection(playerSocket));
			}
			catch (IOException e)
			{
				if (isInterrupted())
				{
					LOGGER.finer("Server is interrupted.");
					break;
				}
				LOGGER.warning("Could not accept client connection: " + e.getMessage());
				// Continue
			}
		}
	}
	
	/**
	 * Closes the server.
	 * @throws IOException if there was a problem
	 */
	public void close() throws IOException
	{
		gamesManager.closeAllConnections();
		this.interrupt();
		serverSocket.close();
	}
	
	/**
	 * @return the server port
	 */
	public int getPort()
	{
		return port;
	}
}
