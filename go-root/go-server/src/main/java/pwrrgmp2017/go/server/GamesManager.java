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
import pwrrgmp2017.go.game.Builder.GameFactory;
import pwrrgmp2017.go.server.Exceptions.BadPlayerException;
import pwrrgmp2017.go.server.Exceptions.LostPlayerConnection;
import pwrrgmp2017.go.server.Exceptions.OverridePlayersException;
import pwrrgmp2017.go.server.Exceptions.SameNameException;
import pwrrgmp2017.go.server.connection.LogPlayerHandler;
import pwrrgmp2017.go.server.connection.NotYetPlayingPlayerHandler;
import pwrrgmp2017.go.server.connection.PlayerConnection;
import pwrrgmp2017.go.server.connection.RealPlayerConnection;

public class GamesManager
{
	private static final Logger LOGGER = Logger.getLogger(GamesManager.class.getName());

	private List<Game> games;
	private ConcurrentHashMap<String, PlayerConnection> choosingPlayers;
	private ConcurrentHashMap<String, PlayerConnection> playingPlayers;
	private ConcurrentSkipListMap<String, PlayerConnection> waitingPlayers;
	private int threadCount;

	public GamesManager()
	{
		games = new ArrayList<Game>();
		choosingPlayers = new ConcurrentHashMap<String, PlayerConnection>();
		playingPlayers = new ConcurrentHashMap<String, PlayerConnection>();
		// waitingPlayers = (LinkedHashMap<String, PlayerConnection>)
		// Collections.synchronizedMap(new LinkedHashMap<String,
		// PlayerConnection>());
		waitingPlayers = new ConcurrentSkipListMap<String, PlayerConnection>();
		threadCount = 1;
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
		LogPlayerHandler logPlayerHandler = new LogPlayerHandler(new RealPlayerConnection(socket), this);
		Thread thread = new Thread(logPlayerHandler);
		thread.start();
	}

	// synchronizacja dla uniknięcia dodawania w tym samym czasie 2 tych samych
	// imion
	public synchronized void addChoosingPlayer(PlayerConnection player, String name) throws SameNameException
	{
		for (Entry<String, PlayerConnection> p : waitingPlayers.entrySet())
		{
			if (p.getValue().getPlayerName().equals(name))
				throw new SameNameException();
		}
		if (playingPlayers.containsKey(name))
			throw new SameNameException();
		if (choosingPlayers.putIfAbsent(name, player) != null)
			throw new SameNameException();
		player.setPlayerName(name);

		NotYetPlayingPlayerHandler handler = new NotYetPlayingPlayerHandler((RealPlayerConnection) player, this);
		Thread thread = new Thread(handler);
		thread.start();
	}

	public boolean inviteSecondPlayer(String invitedName, PlayerConnection inviter, String gameInfo)
			throws BadPlayerException
	{
		if (!choosingPlayers.contains(inviter))
			throw new BadPlayerException();
		PlayerConnection invited = choosingPlayers.get(invitedName);
		if (invited == null)
			throw new BadPlayerException();
		// return invited.invite(inviter.getPlayerName(), gameInfo);
		return false;
	}

	public void waitForGame(PlayerConnection player, String gameInfo) throws BadPlayerException
	{
		PlayerConnection secondPlayer;
		choosingPlayers.remove(player.getPlayerName());
		while (true)
		{
			secondPlayer = waitingPlayers.putIfAbsent(gameInfo, player);
			if (secondPlayer != null)
			{
				// if the secondPlayer disappeared, try again
				if (waitingPlayers.remove(gameInfo, secondPlayer) == false)
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
			throws BadPlayerException // Playe'rzy nie mogą byc w zadnej mapie
	{
		if (playingPlayers.putIfAbsent(player.getPlayerName(), player) == null)
			throw new BadPlayerException();
		if (opponent != null)
			if (playingPlayers.putIfAbsent(opponent.getPlayerName(), opponent) == null)
				throw new BadPlayerException();

		GameFactory director = GameFactory.getInstance();
		// gameinfo: bot/opponent?, Japan/Chinese/...?, boardSize=?,
		// komi(std=6,5pkt)?, czasNaRuch??,
		GameController gameController = director.createGame(gameInfo);
		Game game = new Game(gameController, Integer.toString(threadCount));
		threadCount++;
		games.add(game);
		try
		{
			game.setPlayers(player, opponent);
		}
		catch (OverridePlayersException e)
		{
			e.printStackTrace(); // Nigdy sie nie wykona
		}
	}

	public void deleteGame(Game game)
	{
		PlayerConnection player1 = game.getPlayerConnection(1);
		PlayerConnection player2 = game.getPlayerConnection(2);
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
		String playerName = player.getPlayerName();
		// String gameInfo = player.getGameInfo();
		String gameInfo = "";
		if (choosingPlayers.remove(playerName, player) == false)
			if (waitingPlayers.remove(gameInfo, player) == false)
				if (playingPlayers.remove(playerName, player) == false)
					throw new LostPlayerConnection();
	}
}
