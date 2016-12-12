package pwrrgmp2017.go.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import pwrrgmp2017.go.server.playerobserver.IPlayerObserver;
import pwrrgmp2017.go.server.playerobserver.PlayerEvent;

public class RealPlayerConnection extends PlayerConnection
{
	protected static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());

	protected Socket socket;
	protected BufferedReader input;
	protected PrintWriter output;

	protected static int id = 0;

	protected ConcurrentHashMap<String, List<IPlayerObserver>> eventObserverLists;

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
		}
		catch (IOException e)
		{
			throw e;
		}

		this.eventObserverLists = new ConcurrentHashMap<String, List<IPlayerObserver>>();

		this.start();
	}

	@Override
	public void run()
	{
		String message;
		while (!isInterrupted())
		{
			try
			{
				message = input.readLine();
				if (message == null)
				{
					LOGGER.warning("Client closed the connection.");
					close();
					return;
				}

				output.println("Received: " + message);
				LOGGER.info("Received from client " + getPlayerName() + ": " + message);

				if (message.startsWith("LEAVE"))
				{
					LOGGER.warning("Player " + getPlayerName() + " wants to leave.");
					close();
					return;
				}
			}
			catch (IOException e)
			{
				if (isInterrupted())
				{
					LOGGER.finer("Player " + getPlayerName() + " connection thread interrupted.");
					break;
				}
				LOGGER.warning("Player " + getPlayerName() + " connection error: " + e.getMessage());
				close();
				return;
			}
		}
	}

	@Override
	public void close()
	{
		LOGGER.warning("Ending player " + getPlayerName() + " connection.");

		output.println("exit");

		this.interrupt();
		try
		{
			socket.close();
		}
		catch (IOException e)
		{
			LOGGER.warning("Could not close socket " + getPlayerName() + ": " + e.getMessage());
		}
	}

	@Override
	public PlayerInfo getPlayerInfo()
	{
		return this.player;
	}

	public void addObserverOn(IPlayerObserver observer, PlayerEvent event)
	{
		String eventName = event.getClass().getName();

		synchronized (eventObserverLists)
		{
			if (eventObserverLists.get(eventName) == null)
			{
				eventObserverLists.put(eventName, Collections.synchronizedList(new LinkedList<IPlayerObserver>()));
			}

			eventObserverLists.get(eventName).add(observer);
		}
	}

	public void removeObserverOn(IPlayerObserver observer, PlayerEvent event)
	{
		String eventName = event.getClass().getName();

		synchronized (eventObserverLists)
		{
			if (eventObserverLists.get(eventName) == null)
			{
				return;
			}

			eventObserverLists.get(eventName).remove(observer);
		}
	}

	@Override
	public boolean invite(String inviterName, String gameInfo)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
