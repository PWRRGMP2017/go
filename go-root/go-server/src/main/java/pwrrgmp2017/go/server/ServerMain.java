package pwrrgmp2017.go.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Runs the server.
 */
public class ServerMain
{
	private static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());

	public static void main(String[] args)
	{
		if (args.length != 1)
		{
			printUsage();
			return;
		}

		int port;
		try
		{
			port = Integer.parseUnsignedInt(args[0]);
		}
		catch (NumberFormatException e)
		{
			LOGGER.log(Level.SEVERE, "Port must be a number: {0}", args[0]);
			return;
		}

		if (port < 5001)
		{
			LOGGER.log(Level.SEVERE, "Post must be greater than 5000: {0}", port);
			return;
		}

		LOGGER.log(Level.INFO, "Server is starting.");
		ServerSocket serverSocket;
		try
		{
			serverSocket = new ServerSocket(port);
			LOGGER.log(Level.INFO, "Server is running.");
		}
		catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, "Could not create server socket: " + e.getMessage());
			return;
		}

		Socket playerSocket;
		while (true)
		{
			try
			{
				playerSocket = serverSocket.accept();
				new RealPlayerConnection(playerSocket);
			}
			catch (IOException e)
			{
				LOGGER.log(Level.WARNING, "Could not accept client connection: " + e.getMessage());
				// Continue
			}
		}
	}

	private static void printUsage()
	{
		System.out.println("usage: java go-server.jar port");
	}

}
