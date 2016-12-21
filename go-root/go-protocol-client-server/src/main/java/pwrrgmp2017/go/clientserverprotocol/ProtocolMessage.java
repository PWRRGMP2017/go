package pwrrgmp2017.go.clientserverprotocol;

import pwrrgmp2017.go.game.factory.GameInfo;

/**
 * Interface for messages in server-client communication.
 */
public abstract class ProtocolMessage
{
	/**
	 * Delimiter between the certain parts of the full message.
	 */
	private static final String DELIMITER = ";";

	/**
	 * Returns the delimiter between the certain parts of the message.
	 * 
	 * @return delimiter
	 */
	public static final String getDelimiter()
	{
		return DELIMITER;
	}

	public static final ProtocolMessage getProtocolMessage(String message)
	{
		String[] parts = message.split(DELIMITER);
		// for (String part : parts)
		// {
		// System.out.println(part);
		// }

		if (parts[0].equals(LoginProtocolMessage.getCommand()))
		{
			return new LoginProtocolMessage(parts[1]);
		}
		else if (parts[0].equals(LoginResponseProtocolMessage.getCommand()))
		{
			return new LoginResponseProtocolMessage(Boolean.valueOf(parts[1]), parts[2]);
		}
		else if (parts[0].equals(ExitProtocolMessage.getCommand()))
		{
			return new ExitProtocolMessage();
		}
		else if (parts[0].equals(InvitationProtocolMessage.getCommand()))
		{
			GameInfo gameInfo = new GameInfo(getRestOfStringFrom(3, parts, GameInfo.DELIMITER));

			return new InvitationProtocolMessage(parts[1], parts[2], gameInfo);
		}
		else if (parts[0].equals(ResignProtocolMessage.getCommand()))
		{
			return new ResignProtocolMessage(parts[1]);
		}
		else if (parts[0].equals(InvitationResponseProtocolMessage.getCommand()))
		{
			return new InvitationResponseProtocolMessage(Boolean.valueOf(parts[1]), parts[2]);
		}
		else if (parts[0].equals(ConfirmationProtocolMessage.getCommand()))
		{
			return new ConfirmationProtocolMessage();
		}
		else if (parts[0].equals(MoveProtocolMessage.getCommand()))
		{
			return new MoveProtocolMessage(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
		}
		else if (parts[0].equals(ChangeTerritoryProtocolMessage.getCommand()))
		{
			return new ChangeTerritoryProtocolMessage(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
		}
		else if (parts[0].equals(AcceptTerritoryProtocolMessage.getCommand()))
		{
			return new AcceptTerritoryProtocolMessage();
		}
		else if (parts[0].equals(PassProtocolMessage.getCommand()))
		{
			return new PassProtocolMessage();
		}
		else if (parts[0].equals(ResumeGameProtocolMessage.getCommand()))
		{
			return new ResumeGameProtocolMessage();
		}
		else if (parts[0].equals(PlayBotGameProtocolMessage.getCommand()))
		{
			GameInfo gameInfo = new GameInfo(getRestOfStringFrom(2, parts, GameInfo.DELIMITER));
			
			return new PlayBotGameProtocolMessage(parts[1], gameInfo);
		}
		else if (parts[0].equals(WaitForGameProtocolMessage.getCommand()))
		{
			GameInfo gameInfo = new GameInfo(getRestOfStringFrom(1, parts, GameInfo.DELIMITER));
			
			return new WaitForGameProtocolMessage(gameInfo);
		}
		else if (parts[0].equals(PlayerFoundProtocolMessage.getCommand()))
		{
			return new PlayerFoundProtocolMessage(parts[1], Boolean.valueOf(parts[2]));
		}
		else if (parts[0].equals(CancelWaitingProtocolMessage.getCommand()))
		{
			return new CancelWaitingProtocolMessage();
		}
		else if (parts[0].equals(CancelWaitingResponseProtocolMessage.getCommand()))
		{
			return new CancelWaitingResponseProtocolMessage(Boolean.valueOf(parts[1]));
		}
		else
		{	
			return new UnknownProtocolMessage(message);
		}
	}
	
	private static String getRestOfStringFrom(int i, String[] parts, String delimiter)
	{
		StringBuilder result = new StringBuilder();
		for ( /* i = i */; i < parts.length; ++i)
		{
			result.append(parts[i] + delimiter);
		}
		return result.toString();
	}

	/**
	 * Command of the message, e.g. "LOGIN".
	 * 
	 * @return message command
	 */
	public static String getCommand()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Full message that can be sent or interpreted, e.g.
	 * "LOGIN;username;password".
	 * 
	 * @return full message
	 */
	public abstract String getFullMessage();
}
