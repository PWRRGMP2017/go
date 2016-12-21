package pwrrgmp2017.go.server.connection;

import java.io.IOException;

public class BotPlayerConnection extends PlayerConnection
{
	public BotPlayerConnection()
	{
		playerInfo = new PlayerInfo("Bot");
	}

	@Override
	public void close()
	{
		// do nothing
	}

	@Override
	public String receive() throws IOException
	{
		return "";
		// do nothing
	}

	@Override
	public void send(String message)
	{
		// do nothing
	}
	
}
