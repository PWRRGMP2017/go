package pwrrgmp2017.go.clientserverprotocol;

/**
 * Used to represent not defined {@link ProtocolMessage}.
 */
public class UnknownProtocolMessage extends ProtocolMessage
{
	private static final String COMMAND = "";

	private String fullMessage;

	public UnknownProtocolMessage(String fullMessage)
	{
		this.fullMessage = fullMessage;
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
