package pwrrgmp2017.go.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import pwrrgmp2017.go.game.GameController;
import pwrrgmp2017.go.game.factory.GameFactory;
import pwrrgmp2017.go.game.factory.GameInfo;
import pwrrgmp2017.go.server.Exceptions.BadPlayerException;
import pwrrgmp2017.go.server.Exceptions.LostPlayerConnection;
import pwrrgmp2017.go.server.Exceptions.SameNameException;
import pwrrgmp2017.go.server.Exceptions.tooLateToBackPlayerException;
import pwrrgmp2017.go.server.connection.BotPlayerConnection;
import pwrrgmp2017.go.server.connection.LogPlayerHandler;
import pwrrgmp2017.go.server.connection.NotYetPlayingPlayerHandler;
import pwrrgmp2017.go.server.connection.PlayerConnection;
import pwrrgmp2017.go.server.connection.RealPlayerConnection;

/**
 * Manages list of players and games on the server. Singleton.
 */
public class GamesManager
{
	private static volatile GamesManager INSTANCE;
	
	/**
	 * List of games currently being played.
	 */
	private List<Game> games;

	/**
	 * Map of players currently not playing (presumably, they are changing the
	 * game settings and/or responding to invitations).
	 */
	private ConcurrentHashMap<String, PlayerConnection> choosingPlayers;

	/**
	 * Map of players currently playing.
	 */
	private ConcurrentHashMap<String, PlayerConnection> playingPlayers;

	/**
	 * Map of players currently searching for another player with the same game
	 * settings.
	 */
	private ConcurrentSkipListMap<String, PlayerConnection> waitingPlayers;

	/**
	 * Constructor.
	 */
	private GamesManager()
	{
		games = new ArrayList<Game>();
		choosingPlayers = new ConcurrentHashMap<String, PlayerConnection>();
		playingPlayers = new ConcurrentHashMap<String, PlayerConnection>();
		waitingPlayers = new ConcurrentSkipListMap<String, PlayerConnection>();
	}
	
	public static GamesManager getInstance()
	{
		if (INSTANCE == null)
		{
            synchronized (GamesManager.class)
            {
                if (INSTANCE == null)
                {
                	INSTANCE = new GamesManager();
                }
            }
        }
        return INSTANCE;
	}

	/**
	 * Closes all player connections. Basically resets the games manager.
	 */
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

	/**
	 * Creates a player connection and lets the {@link LogPlayerHandler} thread
	 * handle the login process. The Games Manager does not care about the
	 * player until he is successfully logged in.
	 * 
	 * @param socket
	 *            player connection socket
	 * @throws IOException
	 *             if there was a problem with the connection (already!)
	 */
	public void createPlayerConnection(Socket socket) throws IOException
	{
		LogPlayerHandler logPlayerHandler = new LogPlayerHandler(new RealPlayerConnection(socket), this);
		Thread thread = new Thread(logPlayerHandler);
		thread.start();
	}

	/**
	 * Adds a player to the list of not yet playing players. A thread
	 * {@link NotYetPlayingHandler} is started.
	 * 
	 * @param player
	 *            player connection
	 * @param name
	 *            name of the player
	 * @throws SameNameException
	 *             if the player with this name already exists on the server
	 */
	public synchronized void addChoosingPlayer(PlayerConnection player, String name) throws SameNameException
	{
		if (player instanceof BotPlayerConnection)
		{
			return;
		}

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

	/**
	 * Lets the not playing player play with bot. Starts a {@link Game} thread.
	 * 
	 * @param player
	 *            player connection
	 * @param gameInfo
	 *            information about the game
	 * @throws BadPlayerException
	 *             if the player is not on the list of not playing players
	 */
	public void playBotGame(PlayerConnection player, GameInfo gameInfo) throws BadPlayerException
	{
		if (!choosingPlayers.contains(player))
			throw new BadPlayerException();
		if (!choosingPlayers.remove(player.getPlayerName(), player))
			throw new BadPlayerException();

		BotPlayerConnection bot = new BotPlayerConnection();

		// Create the game
		GameFactory director = GameFactory.getInstance();
		GameController gameController = director.createGame(gameInfo.getAsString());
		Game game = new Game(player, bot, gameController, this);
		games.add(game);

		// Clean up
		player.cancelInvitation();

		// Set state
		player.getPlayerInfo().setPlayingGame(game);
		bot.getPlayerInfo().setPlayingGame(game);

		// Finally, let's start the game thread
		game.start();
	}

	/**
	 * Gets a not playing player connection from the list.
	 * 
	 * @param playerName
	 *            the name of the player
	 * @return player connection
	 * @throws BadPlayerException
	 *             if the player was not found in the list
	 */
	public PlayerConnection getChoosingPlayer(String playerName) throws BadPlayerException
	{
		PlayerConnection playerConnection = choosingPlayers.get(playerName);
		if (playerConnection == null)
		{
			throw new BadPlayerException("The player does not exists or is already playing.");
		}

		return playerConnection;
	}

	/**
	 * Lets the player wait for another player.
	 * 
	 * @param player
	 *            the name of the player
	 * @param gameInfo
	 *            information about the game settings
	 * @return player connection if a suitable player was found, null if the
	 *         player must wait
	 */
	public PlayerConnection waitForGame(PlayerConnection player, GameInfo gameInfo)
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
					// The handler thread should take care of creating the game
					return secondPlayer;
					// createGame(player, secondPlayer, gameInfo);
				}
			}
			break;
		}
		return null;
	}
	
	/**
	 * Removes a player from the waiting list, if the game for him is not already created.
	 * @param player the name of the player
	 * @param gameInfo information about the game he's waiting for
	 * @throws tooLateToBackPlayerException if the player is already in the process of game creation
	 */
	public void stopWaiting(PlayerConnection player, String gameInfo) throws tooLateToBackPlayerException
	{
		if (waitingPlayers.remove(gameInfo, player))
		{
			choosingPlayers.put(player.getPlayerName(), player);
		}
		else
			throw new tooLateToBackPlayerException();
	}
	
	/**
	 * Creates a game with two real players and starts the {@link Game} thread.
	 * @param blackPlayer black player connection
	 * @param whitePlayer white player connection
	 * @param gameInfo information about the game
	 * @throws BadPlayerException if at least one of the players is already playing
	 */
	public void createGame(PlayerConnection blackPlayer, PlayerConnection whitePlayer, GameInfo gameInfo)
			throws BadPlayerException
	{
		// Make sure the players are in the right state
		if (/*
			 * !choosingPlayers.contains(blackPlayer) ||
			 * !choosingPlayers.contains(whitePlayer) ||
			 */
		playingPlayers.contains(blackPlayer) || playingPlayers.contains(whitePlayer))
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
	
	/**
	 * Ends a game and resets the players states so they can play again on the server.
	 * @param game game to remove
	 */
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
	
	/**
	 * Removes a player completely from the list. Note: it does not close a connection.
	 * @param player the player to remove
	 * @throws LostPlayerConnection if the player was not found in any list
	 */
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
