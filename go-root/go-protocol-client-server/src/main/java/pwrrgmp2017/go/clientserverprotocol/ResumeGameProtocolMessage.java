package pwrrgmp2017.go.clientserverprotocol;

public class ResumeGameProtocolMessage extends ProtocolMessage
{
	private static final String COMMAND = "RESUME GAME";

	private String fullMessage;

	public ResumeGameProtocolMessage()
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
