package pwrrgmp2017.go.clientserverprotocol;

/**
 * Used to inform a player that a player was found for him to play with.
 */
public class PlayerFoundProtocolMessage extends ProtocolMessage
{
	private static final String COMMAND = "PLAYER FOUND";

	private String fullMessage;
	private String opponentName;
	private boolean isYourColorBlack;

	public PlayerFoundProtocolMessage(String opponentName, boolean isYourColorBlack)
	{
		this.opponentName = opponentName;
		this.isYourColorBlack = isYourColorBlack;
		this.fullMessage = COMMAND + getDelimiter() + opponentName + getDelimiter() + isYourColorBlack;
	}

	public static String getCommand()
	{
		return COMMAND;
	}
	
	public String getOpponentName()
	{
		return opponentName;
	}
	
	public boolean getIsYourColorBlack()
	{
		return isYourColorBlack;
	}

	@Override
	public String getFullMessage()
	{
		return fullMessage;
	}
}
