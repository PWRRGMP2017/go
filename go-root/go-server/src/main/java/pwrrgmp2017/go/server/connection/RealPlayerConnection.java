package pwrrgmp2017.go.server.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

import pwrrgmp2017.go.server.ServerMain;

public class RealPlayerConnection extends PlayerConnection
{
	protected static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());

	protected Socket socket;
	protected BufferedReader input;
	protected PrintWriter output;

	public RealPlayerConnection(Socket socket) throws IOException
	{
		this.socket = socket;
		this.playerInfo = new PlayerInfo("");
		createInputOutput();
	}

	protected void createInputOutput() throws IOException
	{
		this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.output = new PrintWriter(socket.getOutputStream(), true);
	}

	public String receive() throws IOException
	{
		return input.readLine();
	}

	public synchronized void send(String message)
	{
		output.println(message);
	}

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

	public boolean isClosed()
	{
		return socket.isClosed();
	}

}
