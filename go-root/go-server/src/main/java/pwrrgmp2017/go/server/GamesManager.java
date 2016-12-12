package pwrrgmp2017.go.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Logger;

import pwrrgmp2017.go.game.GameController;
import pwrrgmp2017.go.game.Builder.GameBuilderDirector;
import pwrrgmp2017.go.server.Exceptions.BadPlayerException;
import pwrrgmp2017.go.server.Exceptions.LostPlayerConnection;
import pwrrgmp2017.go.server.Exceptions.OverridePlayersException;
import pwrrgmp2017.go.server.Exceptions.SameNameException;

public class GamesManager
{
	private static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());

	private List<Game> games;
	private ConcurrentHashMap<String, PlayerConnection> choosingPlayers;
	private ConcurrentHashMap<String, PlayerConnection> playingPlayers;
	private ConcurrentSkipListMap<String, PlayerConnection> waitingPlayers;
	Integer threadCount;

	GamesManager()
	{
		games = new ArrayList<Game>();
		choosingPlayers = new ConcurrentHashMap<String, PlayerConnection>();
		playingPlayers = new ConcurrentHashMap<String, PlayerConnection>();
		//waitingPlayers = (LinkedHashMap<String, PlayerConnection>) Collections.synchronizedMap(new LinkedHashMap<String, PlayerConnection>());
		waitingPlayers = new ConcurrentSkipListMap<String, PlayerConnection>();
		threadCount=1;
	}

	public void closeAllConnections()
	{
		for (Game game : games)
			deleteGame(game);
		
		for (PlayerConnection connection : playingPlayers.values())
			connection.close();

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
			connection.close();

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
	
	public synchronized void addChoosingPlayer(PlayerConnection player, String name) throws SameNameException //synchronizacja dla uniknięcia dodawania w tym samym czasie 2 tych samych imion
	{
		for(Entry<String, PlayerConnection> p : waitingPlayers.entrySet())
		{
			if(p.getValue().getGameInfo().equals(name))
				throw new SameNameException();
		}
		if(playingPlayers.containsKey(name))
			throw new SameNameException();
		if(choosingPlayers.putIfAbsent(name, player)!=null)
			throw new SameNameException();
		player.setPlayerName(name);
	}
	
	public boolean inviteSecondPlayer(String invitedName, PlayerConnection inviter, String gameInfo) throws BadPlayerException
	{
		if(!choosingPlayers.contains(inviter))
			throw new BadPlayerException();
		PlayerConnection invited=choosingPlayers.get(invitedName);
		if(invited==null)
			throw new BadPlayerException();
		return invited.invite(inviter.getPlayerName(), gameInfo);
	}
	
	public void waitForGame(PlayerConnection player, String gameInfo) throws BadPlayerException
	{
		PlayerConnection secondPlayer;
		choosingPlayers.remove(player.getPlayerName());
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
	
	public void createGame(PlayerConnection player, PlayerConnection opponent, String gameInfo) throws BadPlayerException //Playe'rzy nie mogą byc w zadnej mapie
	{
		if(playingPlayers.putIfAbsent(player.getPlayerName(), player) == null)
			throw new BadPlayerException();
		if(playingPlayers.putIfAbsent(opponent.getPlayerName(), opponent) == null)
			throw new BadPlayerException();
		
		GameBuilderDirector director=GameBuilderDirector.getInstance();
		GameController  gameController= director.createGame(gameInfo);
		Game game= new Game(gameController, threadCount.toString());
		threadCount++;
		games.add(game);
		try
		{
			game.setPlayers(player, opponent);
		}
		catch (OverridePlayersException e)
		{
			e.printStackTrace(); //Nigdy sie nie wykona
		}
	}

	public void deleteGame(Game game)
	{
		PlayerConnection player1=game.getPlayerConnection(1);
		PlayerConnection player2=game.getPlayerConnection(2);
		try
		{
			game.addExitMessage(player1);
			game.addExitMessage(player2);
		}
		catch (BadPlayerException e)
		{
			e.printStackTrace();
		}
		games.remove(game);
		playingPlayers.remove(player1.getPlayerName());
		playingPlayers.remove(player2.getPlayerName());
	}
	
	public void deletePlayer(PlayerConnection player) throws LostPlayerConnection
	{
		String playerName=player.getPlayerName();
		String gameInfo=player.getGameInfo();
		if(choosingPlayers.remove(playerName, player)==false)
			if(waitingPlayers.remove(gameInfo, player)==false)
				if(playingPlayers.remove(playerName, player)==false)
					throw new LostPlayerConnection();
	}
}
