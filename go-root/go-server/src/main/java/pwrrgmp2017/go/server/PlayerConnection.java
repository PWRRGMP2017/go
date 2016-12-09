package pwrrgmp2017.go.server;

import pwrrgmp2017.go.server.playerobserver.IPlayerObservable;

public abstract class PlayerConnection extends Thread implements IPlayerObservable
{
	PlayerInfo player;
	String gameInfo;
	// ID Player for GamesManager?

	void addMessage(String msg)
	{
		// TODO
	}

	String translateMessage(String msg)
	{
		// TODO
		return null;
	}

	String getPlayerName()
	{
		return player.getName();
	}

	public abstract void close();

	public abstract PlayerInfo getPlayerInfo();
	
	public abstract void invite(String inviterName, String gameInfo);

}