package pwrrgmp2017.go.clientserverprotocol;

/**
 * Used when player wants to cancel searching for another player.
 */
public class CancelWaitingProtocolMessage extends ProtocolMessage
{
	private static final String COMMAND = "CANCEL WAITING";

	private String fullMessage;

	public CancelWaitingProtocolMessage()
	{
		this.fullMessage = COMMAND;
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
