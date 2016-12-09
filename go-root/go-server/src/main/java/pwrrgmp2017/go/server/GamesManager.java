package pwrrgmp2017.go.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import pwrrgmp2017.go.server.Exceptions.LostPlayerConnection;
import pwrrgmp2017.go.server.Exceptions.SameNameException;

public class GamesManager
{
	private static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());

	private List<Game> games;
	private ConcurrentHashMap<String, PlayerConnection> choosingPlayers;
	private ConcurrentHashMap<String, PlayerConnection> playingPlayers;
	private ConcurrentHashMap<String, PlayerConnection> waitingPlayers;

	GamesManager()
	{
		games = new ArrayList<Game>();
		choosingPlayers = new ConcurrentHashMap<String, PlayerConnection>();
		playingPlayers = new ConcurrentHashMap<String, PlayerConnection>();
		//waitingPlayers = (LinkedHashMap<String, PlayerConnection>) Collections.synchronizedMap(new LinkedHashMap<String, PlayerConnection>());
		waitingPlayers = new ConcurrentHashMap<String, PlayerConnection>();
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
	
	public synchronized void addChoosingPlayer(PlayerConnection player) throws SameNameException //synchronizacja dla uniknięcia dodawania w tym samym czasie 2 tych samych imion
	{
		String name=player.getPlayerName();
		if(waitingPlayers.containsKey(name))
			throw new SameNameException();
		if(playingPlayers.containsKey(name))
			throw new SameNameException();
		if(choosingPlayers.putIfAbsent(player.getName(), player)!=null)
			throw new SameNameException();
	}
	
	public boolean inviteSecondPlayer(String invitedName, PlayerConnection inviter, String gameInfo)
	{
		return false;
	}
	
	public void waitForGame(PlayerConnection player, String gameInfo)
	{
		PlayerConnection secondPlayer;
		while(true)
		{
			secondPlayer=waitingPlayers.putIfAbsent(gameInfo, player);
			if(secondPlayer!=null)
			{
				if(waitingPlayers.remove(gameInfo, secondPlayer)==false) //if the secondPlayer disappeared, try again
					continue;
				else
				{
					createGame(player, secondPlayer, gameInfo);
				}
			}
			break;
		}
	}
	

	public void createGame(PlayerConnection player, PlayerConnection opponent, String gameInfo)
	{

	}

	private void deletePlayer(PlayerConnection player) throws LostPlayerConnection
	{
		//TODO
	}
}
