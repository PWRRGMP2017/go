package pwrrgmp2017.go.clientserverprotocol;

public class ChangeTerritoryProtocolMessage extends ProtocolMessage
{
	private static final String COMMAND = "CHANGE TERRITORY";

	private String fullMessage;
	private int x;
	private int y;

	public ChangeTerritoryProtocolMessage(int x, int y)
	{
		this.x = x;
		this.y = y;
		this.fullMessage = COMMAND + getDelimiter() + x + getDelimiter() + y;
	}

	public static String getCommand()
	{
		return COMMAND;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	@Override
	public String getFullMessage()
	{
		return fullMessage;
	}
}
