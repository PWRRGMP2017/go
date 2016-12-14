package pwrrgmp2017.go.server.connection;

import java.io.IOException;

public abstract class PlayerConnection
{
	protected PlayerInfo playerInfo;

	public PlayerInfo getPlayerInfo()
	{
		return playerInfo;
	}

	public String getPlayerName()
	{
		return playerInfo.getName();
	}

	public void setPlayerName(String name)
	{
		playerInfo.setPlayerName(name);
	}

	public abstract void close();

	public abstract String receive() throws IOException;

	public abstract void send(String message);

}
