package pwrrgmp2017.go.clientserverprotocol;

/**
 * Used when the player accepts the territory.
 */
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
