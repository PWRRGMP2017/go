package pwrrgmp2017.go.clientserverprotocol;

import pwrrgmp2017.go.game.factory.GameInfo;

/**
 * Used when a player wants to search for another player to play with.
 */
public class WaitForGameProtocolMessage extends ProtocolMessage
{
	private static final String COMMAND = "WAIT FOR GAME";

	private String fullMessage;
	private GameInfo gameInfo;

	public WaitForGameProtocolMessage(GameInfo gameInfo)
	{
		this.gameInfo = gameInfo;
		this.fullMessage = COMMAND + getDelimiter() + gameInfo.getAsString();
	}

	public static String getCommand()
	{
		return COMMAND;
	}
	
	public GameInfo getGameInfo()
	{
		return gameInfo;
	}

	@Override
	public String getFullMessage()
	{
		return fullMessage;
	}
}
