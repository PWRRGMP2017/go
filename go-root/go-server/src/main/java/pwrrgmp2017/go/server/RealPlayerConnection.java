package pwrrgmp2017.go.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class RealPlayerConnection extends PlayerConnection
{
	private static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());

	Socket socket;
	BufferedReader input;
	PrintWriter output;

	static int id = 0;

	public RealPlayerConnection(Socket socket) throws IOException
	{
		if (socket == null)
		{
			throw new NullPointerException("Player connection socket cannot be null!");
		}

		this.socket = socket;
		this.player = new PlayerInfo(Integer.toString(id++));
		try
		{
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream(), true);
			output.println("WELCOME");
		}
		catch (IOException e)
		{
			throw e;
		}
	}

	public void run()
	{
		String message;
		while (true)
		{
			try
			{
				message = input.readLine();
				output.println("Received: " + message);
				LOGGER.info("Received from client " + getPlayerName() + ": " + message);

				if (message.startsWith("LEAVE"))
				{
					LOGGER.warning("Player " + getPlayerName() + " wants to leave.");
					endConnection();
					return;
				}
			}
			catch (IOException e)
			{
				LOGGER.warning("Player " + getPlayerName() + " connection error: " + e.getMessage());
				endConnection();
				return;
			}
		}
	}

	private void endConnection()
	{
		LOGGER.warning("Ending player " + getPlayerName() + " connection.");
		try
		{
			socket.close();
		}
		catch (IOException e)
		{
			LOGGER.warning("Could not close socket " + getPlayerName() + ": " + e.getMessage());
		}
	}
}
