package pwrrgmp2017.go.server;

public abstract class PlayerConnection extends Thread
{
	Player player;
	Game match;
	// ID Player for GamesManager?

	void addMessage(String msg)
	{
		//TODO
	}

	Game setMatch(Game match) // Ustawia match, z którym będzie sie komunikował
	{
		return this.match= match;
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
