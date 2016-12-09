package pwrrgmp2017.go.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import pwrrgmp2017.go.server.Exceptions.LostPlayerConnection;

public class GamesManager
{
	private static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());

	private List<Game> games;
	private ConcurrentHashMap<String, PlayerConnection> choosingPlayers;
	private ConcurrentHashMap<String, PlayerConnection> playingPlayers;
	private Map<String, PlayerConnection> waitingPlayers;

	GamesManager()
	{
		games = new ArrayList<Game>();
		choosingPlayers = new ConcurrentHashMap<String, PlayerConnection>();
		playingPlayers = new ConcurrentHashMap<String, PlayerConnection>();
		waitingPlayers = Collections.synchronizedMap(new LinkedHashMap<String, PlayerConnection>());
	}

	public void closeAllConnections()
	{
		for (PlayerConnection connection : playingPlayers.values())
		{
			connection.close();
		}

		for (PlayerConnection connection : choosingPlayers.values())
		{
			connection.close();
			try
			{
				connection.join();
			}
			catch (InterruptedException e)
			{
				LOGGER.warning("Interrupted join: " + e.getMessage());
			}
		}

		for (PlayerConnection connection : waitingPlayers.values())
		{
			connection.close();
		}

		for (Game game : games)
		{
			// TODO
		}

		playingPlayers.clear();
		choosingPlayers.clear();
		waitingPlayers.clear();
		games.clear();
	}

	public void createPlayerConnection(Socket socket) throws IOException
	{
		// choosingPlayers.add(new RealPlayerConnection(socket));
		choosingPlayers.put("test", new RealPlayerConnection(socket));
	}

	public void createGame(PlayerConnection player, PlayerConnection opponent, Object typeGame)
	{

	}

	private void deletePlayer(PlayerConnection player) throws LostPlayerConnection
	{

	}
}
