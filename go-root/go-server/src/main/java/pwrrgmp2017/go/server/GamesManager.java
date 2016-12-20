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
import pwrrgmp2017.go.game.factory.GameFactory;
import pwrrgmp2017.go.game.factory.GameInfo;
import pwrrgmp2017.go.server.Exceptions.BadPlayerException;
import pwrrgmp2017.go.server.Exceptions.LostPlayerConnection;
import pwrrgmp2017.go.server.Exceptions.SameNameException;
import pwrrgmp2017.go.server.Exceptions.tooLateToBackPlayerException;
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

	public GamesManager()
	{
		games = new ArrayList<Game>();
		choosingPlayers = new ConcurrentHashMap<String, PlayerConnection>();
		playingPlayers = new ConcurrentHashMap<String, PlayerConnection>();
		waitingPlayers = new ConcurrentSkipListMap<String, PlayerConnection>();
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

	public void playBotGame(PlayerConnection player, GameInfo gameInfo) throws BadPlayerException
	{
		if (!choosingPlayers.contains(player))
			throw new BadPlayerException();
		if (!choosingPlayers.remove(player.getPlayerName(), player))
			throw new BadPlayerException();
		createGame(player, null, gameInfo);
	}

	public PlayerConnection getChoosingPlayer(String playerName) throws BadPlayerException
	{
		PlayerConnection playerConnection = choosingPlayers.get(playerName);
		if (playerConnection == null)
		{
			throw new BadPlayerException("The player does not exists or is already playing.");
		}

		return playerConnection;
	}

	public void waitForGame(PlayerConnection player, GameInfo gameInfo) throws BadPlayerException
	{
		PlayerConnection secondPlayer;
		choosingPlayers.remove(player.getPlayerName());
		while (true)
		{
			secondPlayer = waitingPlayers.putIfAbsent(gameInfo.getAsString(), player);
			if (secondPlayer != null)
			{
				// if the secondPlayer disappeared, try again
				if (waitingPlayers.remove(gameInfo.getAsString(), secondPlayer) == false)
					continue;
				else
				{
					createGame(player, secondPlayer, gameInfo);
				}
			}
			break;
		}
	}

	public void stopWaiting(PlayerConnection player, String gameInfo) throws tooLateToBackPlayerException
	{
		if (waitingPlayers.remove(gameInfo, player))
		{
			choosingPlayers.put(player.getPlayerName(), player);
		}
		else
			throw new tooLateToBackPlayerException();
	}

	public void createGame(PlayerConnection blackPlayer, PlayerConnection whitePlayer, GameInfo gameInfo)
			throws BadPlayerException
	{
		// Make sure the players are in the right state
		if (!choosingPlayers.contains(blackPlayer) || !choosingPlayers.contains(whitePlayer)
				|| playingPlayers.contains(blackPlayer) || playingPlayers.contains(whitePlayer))
		{
			throw new BadPlayerException();
		}

		choosingPlayers.remove(blackPlayer.getPlayerName());
		choosingPlayers.remove(whitePlayer.getPlayerName());

		playingPlayers.put(blackPlayer.getPlayerName(), blackPlayer);
		playingPlayers.put(whitePlayer.getPlayerName(), whitePlayer);

		// Create the game
		GameFactory director = GameFactory.getInstance();
		GameController gameController = director.createGame(gameInfo.getAsString());
		Game game = new Game(blackPlayer, whitePlayer, gameController, this);
		games.add(game);

		// Clean up
		whitePlayer.cancelInvitation();
		blackPlayer.cancelInvitation();

		// Set state
		whitePlayer.getPlayerInfo().setPlayingGame(game);
		blackPlayer.getPlayerInfo().setPlayingGame(game);

		// Finally, let's start the game thread
		game.start();
	}

	public void deleteGame(Game game)
	{
		if (game == null)
		{
			return;
		}

		PlayerConnection whitePlayer = game.getWhitePlayer();
		PlayerConnection blackPlayer = game.getBlackPlayer();
		games.remove(game);
		playingPlayers.remove(whitePlayer.getPlayerName());
		playingPlayers.remove(blackPlayer.getPlayerName());
		whitePlayer.getPlayerInfo().setPlayingGame(null);
		blackPlayer.getPlayerInfo().setPlayingGame(null);
		try
		{
			// Don't need to check if they disconnected, the NotYetPlaying
			// thread will do it for us.
			addChoosingPlayer(whitePlayer, whitePlayer.getPlayerName());
			addChoosingPlayer(blackPlayer, blackPlayer.getPlayerName());
		}
		catch (SameNameException e)
		{
			// Should not happen
			e.printStackTrace();
		}
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
