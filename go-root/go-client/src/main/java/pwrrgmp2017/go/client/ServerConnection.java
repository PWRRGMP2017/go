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
	protected static final Logger LOGGER = Logger.getLogger(ClientMain.class.getName());

	protected String hostname;
	protected int port;

	protected Socket socket;
	protected BufferedReader input;
	protected PrintWriter output;

	public ServerConnection(String hostname, int port) throws IOException
	{
		this.hostname = hostname;
		this.port = port;

		LOGGER.log(Level.INFO, "Server connection is starting.");

		createSocket();
		createInputOutput();

		LOGGER.log(Level.INFO, "Server connection is ok.");
	}

	public String receive() throws IOException
	{
		return input.readLine();
	}

	public void send(String message) throws IOException
	{
		output.println(message);
	}

	public void close()
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

	protected void createSocket() throws IOException
	{
		this.socket = new Socket(hostname, port);
	}

	protected void createInputOutput() throws IOException
	{
		this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.output = new PrintWriter(socket.getOutputStream(), true);
	}
}
