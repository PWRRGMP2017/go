package pwrrgmp2017.go.clientserverprotocol;

public class AcceptTerritoryProtocolMessage extends ProtocolMessage
{
	private static final String COMMAND = "ACCEPT TERRITORY";

	private String fullMessage;

	public AcceptTerritoryProtocolMessage()
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
