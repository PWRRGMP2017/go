package pwrrgmp2017.go.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread
{
	private static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());

	GamesManager gamesManager;

	private int port;
	private ServerSocket serverSocket;

	Server(int port) throws IllegalArgumentException, IOException
	{
		if (port < 5001)
		{
			LOGGER.log(Level.SEVERE, "Post must be greater than 5000: {0}", port);
			throw new IllegalArgumentException();
		}

		this.port = port;
		this.gamesManager = new GamesManager();

		LOGGER.log(Level.INFO, "Server is starting.");
		serverSocket = new ServerSocket(port);

		this.start();
	}

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
				gamesManager.createPlayerConnection(playerSocket);
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

	public void close() throws IOException
	{
		gamesManager.closeAllConnections();
		this.interrupt();
		serverSocket.close();
	}

	public int getPort()
	{
		return port;
	}
}
