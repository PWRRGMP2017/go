package pwrrgmp2017.go.clientserverprotocol;

/**
 * Response for {@link InvitationProtocolMessage}.
 */
public class InvitationResponseProtocolMessage extends ProtocolMessage
{

	private static final String COMMAND = "INVITATION RESPONSE";

	private final boolean isAccepted;
	private final String reason;

	private final String fullMessage;

	public InvitationResponseProtocolMessage(boolean isAccepted, String reason)
	{
		this.isAccepted = isAccepted;
		this.reason = reason;
		this.fullMessage = COMMAND + getDelimiter() + isAccepted + getDelimiter() + reason;
	}

	public boolean getIsAccepted()
	{
		return isAccepted;
	}

	public String getReason()
	{
		return reason;
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
