package pwrrgmp2017.go.server.connection;

import pwrrgmp2017.go.server.Game;

/**
 * Contains information about the player.
 */
public class PlayerInfo
{
	/**
	 * The player name.
	 */
	private String name;
	
	/**
	 * The game the player is currently playing.
	 */
	private volatile Game playingGame;

	/**
	 * Constructor.
	 * @param name the name of the player
	 */
	public PlayerInfo(String name)
	{
		this.name = name;
		playingGame = null;
	}
	
	/**
	 * @return the player name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return true if the player is playing a game currently
	 */
	public boolean isPlaying()
	{
		return playingGame != null;
	}

	/**
	 * @return reference to the game currently being played by the player
	 */
	public Game getPlayingGame()
	{
		return playingGame;
	}
	
	/**
	 * Sets the currently played game by the player.
	 * @param game the game
	 */
	public void setPlayingGame(Game game)
	{
		this.playingGame = game;
	}
	
	/**
	 * Sets the player name.
	 * @param name new name
	 */
	public void setPlayerName(String name)
	{
		this.name = name;
	}
}
