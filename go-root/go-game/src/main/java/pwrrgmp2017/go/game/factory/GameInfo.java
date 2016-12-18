package pwrrgmp2017.go.game.factory;

/**
 * Immutable object containing basic information about a game.
 */
public class GameInfo
{
	public final static String DELIMITER = ";";

	private final int boardSize;
	private final float komiValue;
	private final RulesType rulesType;
	private final boolean isBot;

	private final String asString;

	public enum RulesType
	{
		JAPANESE, CHINESE
	};

	public GameInfo(int boardSize, float komiValue, RulesType rulesType, boolean isBot)
	{
		this.boardSize = boardSize;
		this.komiValue = komiValue;
		this.rulesType = rulesType;
		this.isBot = isBot;
		this.asString = boardSize + DELIMITER + komiValue + DELIMITER + rulesType + DELIMITER + isBot;
	}

	public GameInfo(String asString) throws IllegalArgumentException
	{
		this.asString = asString;
		String[] parts = asString.split(DELIMITER);

		try
		{
			this.boardSize = Integer.parseUnsignedInt(parts[0]);
		}
		catch (NumberFormatException e)
		{
			throw new IllegalArgumentException("boardSize = " + parts[0]);
		}

		try
		{
			this.komiValue = Float.parseFloat(parts[1]);
		}
		catch (NumberFormatException e)
		{
			throw new IllegalArgumentException("komiValue = " + parts[1]);
		}

		this.rulesType = RulesType.valueOf(parts[2]);

		this.isBot = Boolean.getBoolean(parts[3]);
	}

	public int getBoardSize()
	{
		return boardSize;
	}

	public float getKomiValue()
	{
		return komiValue;
	}

	public RulesType getRulesType()
	{
		return rulesType;
	}

	public boolean getIsBot()
	{
		return isBot;
	}

	public String getAsString()
	{
		return asString;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof GameInfo))
		{
			return false;
		}

		GameInfo other = (GameInfo) o;

		return (this.boardSize == other.boardSize) && (this.komiValue == other.komiValue)
				&& (this.rulesType == other.rulesType) && (this.isBot == other.isBot);
	}
}
