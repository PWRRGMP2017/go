package pwrrgmp2017.go.clientserverprotocol;

/**
 * Used when a player wants to pass.
 */
public class PassProtocolMessage extends ProtocolMessage
{
	private static final String COMMAND = "PASS";

	private String fullMessage;

	public PassProtocolMessage()
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
