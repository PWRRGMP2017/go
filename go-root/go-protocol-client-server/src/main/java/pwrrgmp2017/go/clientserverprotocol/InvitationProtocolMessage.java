package pwrrgmp2017.go.clientserverprotocol;

import pwrrgmp2017.go.game.factory.GameInfo;

public class InvitationProtocolMessage extends ProtocolMessage
{
	private static final String COMMAND = "INVITATION";

	private final String fromPlayerName;
	private final String toPlayerName;
	private final GameInfo gameInfo;

	private final String fullMessage;

	public InvitationProtocolMessage(String fromPlayerName, String toPlayerName, GameInfo gameInfo)
	{
		this.fromPlayerName = fromPlayerName;
		this.toPlayerName = toPlayerName;
		this.gameInfo = gameInfo;

		this.fullMessage = COMMAND + getDelimiter() + fromPlayerName + getDelimiter() + toPlayerName + getDelimiter()
				+ this.gameInfo.getAsString();
	}

	public String getFromPlayerName()
	{
		return fromPlayerName;
	}

	public String getToPlayerName()
	{
		return toPlayerName;
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
