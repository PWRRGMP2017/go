package pwrrgmp2017.go.clientserverprotocol;

import java.security.InvalidParameterException;

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

		if (parts[0].equals(LoginProtocolMessage.getCommand()))
		{
			return new LoginProtocolMessage(parts[1]);
		}
		else if (parts[0].equals(LoginResponseProtocolMessage.getCommand()))
		{
			return new LoginResponseProtocolMessage(Boolean.valueOf(parts[1]));
		}
		else
		{
			throw new InvalidParameterException();
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
