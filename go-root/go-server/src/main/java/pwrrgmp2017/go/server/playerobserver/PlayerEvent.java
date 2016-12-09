package pwrrgmp2017.go.server.playerobserver;

import pwrrgmp2017.go.server.Game;
import pwrrgmp2017.go.server.PlayerConnection;
import pwrrgmp2017.go.server.PlayerInfo;

public abstract class PlayerEvent
{
	protected final PlayerConnection playerConnection;
	protected final PlayerInfo playerInfo;
	protected final String message;

	public PlayerEvent(PlayerConnection playerConnection, String message)
	{
		this.message = message;
		this.playerConnection = playerConnection;
		this.playerInfo = playerConnection.getPlayerInfo();
	}

	public PlayerConnection getPlayerConnection()
	{
		return playerConnection;
	}

	public PlayerInfo getPlayerInfo()
	{
		return playerInfo;
	}

	public String getMessage()
	{
		return message;
	}

	public Game getGame()
	{
		return playerInfo.getPlayingGame();
	}
}
