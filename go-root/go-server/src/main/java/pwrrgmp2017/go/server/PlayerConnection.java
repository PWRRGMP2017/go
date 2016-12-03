package pwrrgmp2017.go.server;

public abstract class PlayerConnection extends Thread
{
	Player player;

	void addMessageFromGameModel(String msg)
	{
		//TODO
	}

	String translateMessage(String msg)
	{
		//TODO
		return null;
	}

	String getPlayerInfo()
	{
		return player.getInfo();
	}
}
