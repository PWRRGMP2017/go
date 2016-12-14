package pwrrgmp2017.go.clientserverprotocol;

public class ExitProtocolMessage extends ProtocolMessage
{
	private static final String COMMAND = "EXIT";

	private String fullMessage;

	public ExitProtocolMessage()
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
