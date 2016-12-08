package pwrrgmp2017.go.server;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Scanner;
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
			LOGGER.severe("Port must be a number: " + args[0]);
			return;
		}

		Server server;
		try
		{
			server = new Server(port);
		}
		catch (IOException e)
		{
			LOGGER.severe("Could not create server socket: " + e.getMessage());
			return;
		}
		catch (InvalidParameterException e)
		{
			LOGGER.severe("Invalid argument: " + e.getMessage());
			return;
		}

		Scanner scanner = new Scanner(System.in);
		String input;
		while (true)
		{
			input = scanner.nextLine();
			if (input.equalsIgnoreCase("exit"))
			{
				LOGGER.info("Server is exiting.");
				try
				{
					server.close();
				}
				catch (IOException e)
				{
					LOGGER.warning("Problem occured while closing the server: " + e.getMessage());
				}
				return;
			}
			else
			{
				LOGGER.warning("Unknown input: " + input);
			}
		}
	}

	private static void printUsage()
	{
		System.out.println("usage: java go-server.jar port");
	}

}
