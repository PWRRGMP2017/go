package pwrrgmp2017.go.clientserverprotocol;

/**
 * Used when a player wants to resign.
 */
public class ResignProtocolMessage extends ProtocolMessage
{

	private static final String COMMAND = "RESIGN";

	private String fullMessage;

	private String reason;

	public ResignProtocolMessage(String reason)
	{
		this.reason = reason;
		this.fullMessage = COMMAND + getDelimiter() + reason;
	}

	public static String getCommand()
	{
		return COMMAND;
	}

	public String getReason()
	{
		return reason;
	}

	@Override
	public String getFullMessage()
	{
		return fullMessage;
	}

}
