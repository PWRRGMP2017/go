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
			StringBuilder gameInfoString = new StringBuilder();
			for (int i = 3; i < parts.length; ++i)
			{
				gameInfoString.append(parts[i] + GameInfo.DELIMITER);
			}
			GameInfo gameInfo = new GameInfo(gameInfoString.toString());

			return new InvitationProtocolMessage(parts[1], parts[2], gameInfo);
		}
		else if (parts[0].equals(InvitationResponseProtocolMessage.getCommand()))
		{
			return new InvitationResponseProtocolMessage(Boolean.valueOf(parts[1]), parts[2]);
		}
		else
		{
			return new UnknownProtocolMessage(message);
		}
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
