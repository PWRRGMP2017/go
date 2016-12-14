package pwrrgmp2017.go.server.playerobserver;

import pwrrgmp2017.go.clientserverprotocol.ProtocolMessage;
import pwrrgmp2017.go.server.Game;
import pwrrgmp2017.go.server.PlayerConnection;
import pwrrgmp2017.go.server.PlayerInfo;

public abstract class PlayerEvent
{
	protected final PlayerConnection playerConnection;
	protected final PlayerInfo playerInfo;
	protected final ProtocolMessage message;

	public PlayerEvent(PlayerConnection playerConnection, ProtocolMessage message)
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

	public ProtocolMessage getProtocolMessage()
	{
		return message;
	}

	public Game getGame()
	{
		return playerInfo.getPlayingGame();
	}
}
