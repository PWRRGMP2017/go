package pwrrgmp2017.go.clientserverprotocol;

/**
 * Used as a confirmation from the client or server. It may be used in several ways.
 */
public class ConfirmationProtocolMessage extends ProtocolMessage
{
	private static final String COMMAND = "CONFIRM";

	private String fullMessage;

	public ConfirmationProtocolMessage()
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
