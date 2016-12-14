package pwrrgmp2017.go.server.connection;

import pwrrgmp2017.go.server.Game;

/**
 * Contains info about the player.
 */
public class PlayerInfo
{
	private String name;
	private Game playingGame;

	public PlayerInfo(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public boolean isPlaying()
	{
		return playingGame != null;
	}

	public Game getPlayingGame()
	{
		return playingGame;
	}

	public void setPlayingGame(Game game)
	{
		this.playingGame = game;
	}

	public void setPlayerName(String name)
	{
		this.name=name;
	}
}
