package pwrrgmp2017.go.clientserverprotocol;

public class LoginResponseProtocolMessage extends ProtocolMessage
{
	private static final String COMMAND = "LOGIN RESPONSE";

	private final boolean isAccepted;

	private final String fullMessage;

	public LoginResponseProtocolMessage(boolean isAccepted)
	{
		this.isAccepted = isAccepted;
		this.fullMessage = COMMAND + getDelimiter() + isAccepted;
	}

	public boolean getIsAccepted()
	{
		return isAccepted;
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
