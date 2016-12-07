package pwrrgmp2017.go.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerConnection
{
	private static final Logger LOGGER = Logger.getLogger(ClientMain.class.getName());

	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;

	public ServerConnection(String hostname, int port)
	{
		LOGGER.log(Level.INFO, "Server connection is starting.");
		try
		{
			socket = new Socket(hostname, port);
			LOGGER.log(Level.INFO, "Server connection is ok.");

			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream(), true);

			System.out.println("Server says: " + input.readLine());
		}
		catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, "Could not connect to server: " + e.getMessage());
			return;
		}
	}

	public String receive() throws IOException
	{
		return input.readLine();
	}

	public void send(String message) throws IOException
	{
		output.println(message);
	}

	private void endConnection()
	{
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
}
