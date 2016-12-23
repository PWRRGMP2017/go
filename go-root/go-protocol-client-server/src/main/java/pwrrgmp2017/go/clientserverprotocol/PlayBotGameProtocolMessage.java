package pwrrgmp2017.go.clientserverprotocol;

import pwrrgmp2017.go.game.factory.GameInfo;

/**
 * Used when a player wants to start a game with a bot.
 */
public class PlayBotGameProtocolMessage extends ProtocolMessage
{
	private static final String COMMAND = "PLAY BOT GAME";

	private final String playerName;
	private final GameInfo gameInfo;

	private final String fullMessage;

	public PlayBotGameProtocolMessage(String playerName, GameInfo gameInfo)
	{
		this.playerName = playerName;
		this.gameInfo = gameInfo;

		this.fullMessage = COMMAND + getDelimiter() + playerName + getDelimiter()
				+ this.gameInfo.getAsString();
	}

	public String getPlayerName()
	{
		return playerName;
	}
	public GameInfo getGameInfo()
	{
		return gameInfo;
	}

	public static String getCommand()
	{
		return COMMAND;
	}

	@Override
	public String getFullMessage()
	{
		return fullMessage;
	}
}
