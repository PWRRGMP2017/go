package pwrrgmp2017.go.clientserverprotocol;

public class LoginProtocolMessage extends ProtocolMessage
{
	private static final String COMMAND = "LOGIN";

	private final String username;
	private final String fullMessage;

	public LoginProtocolMessage(String username)
	{
		this.username = username;
		this.fullMessage = COMMAND + getDelimiter() + username;
	}

	public String getUsername()
	{
		return username;
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
