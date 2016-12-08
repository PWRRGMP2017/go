package pwrrgmp2017.go.server;

public abstract class PlayerConnection extends Thread
{
	PlayerInfo player;
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
}
