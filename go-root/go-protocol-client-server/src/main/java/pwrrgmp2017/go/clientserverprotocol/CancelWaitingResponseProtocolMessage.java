package pwrrgmp2017.go.clientserverprotocol;

public class CancelWaitingResponseProtocolMessage extends ProtocolMessage
{
	private static final String COMMAND = "CANCEL WAITING RESPONSE";

	private String fullMessage;
	private boolean isSuccess;

	public CancelWaitingResponseProtocolMessage(boolean isSuccess)
	{
		this.isSuccess = isSuccess;
		this.fullMessage = COMMAND + getDelimiter() + isSuccess;
	}

	public static String getCommand()
	{
		return COMMAND;
	}
	
	public boolean getIsSuccess()
	{
		return isSuccess;
	}

	@Override
	public String getFullMessage()
	{
		return fullMessage;
	}
}
