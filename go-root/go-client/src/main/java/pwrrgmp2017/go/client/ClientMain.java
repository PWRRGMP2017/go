package pwrrgmp2017.go.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Runs the client
 */
public class ClientMain
{
	private static final Logger LOGGER = Logger.getLogger(ClientMain.class.getName());

	public static void main(String[] args)
	{
		if (args.length != 2)
		{
			printUsage();
			return;
		}

		String hostname = args[0];
		int port;
		try
		{
			port = Integer.parseUnsignedInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			LOGGER.log(Level.SEVERE, "Port must be a number: {0}", args[1]);
			return;
		}

		if (port < 5001)
		{
			LOGGER.log(Level.SEVERE, "Post must be greater than 5000: {0}", port);
			return;
		}

		Socket socket;
		BufferedReader input;
		PrintWriter output;

		LOGGER.log(Level.INFO, "Client is starting.");
		try
		{
			socket = new Socket(hostname, port);
			LOGGER.log(Level.INFO, "Client is running.");

			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream(), true);

			System.out.println("Server says: " + input.readLine());
		}
		catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, "Could not connect to server: " + e.getMessage());
			return;
		}

		Scanner scanner = new Scanner(System.in);
		String message;
		while (true)
		{
			try
			{
				System.out.print("Wpisz coś: ");
				message = scanner.nextLine();
				output.println(message);
				System.out.println(input.readLine());
			}
			catch (IOException e)
			{
				LOGGER.log(Level.WARNING, "Problem with server connection: " + e.getMessage());
				return;
				// Continue
			}
		}
	}

	private static void printUsage()
	{
		System.out.println("usage: java go-client.jar hostname port");
	}
}
