package pwrrgmp2017.go.server.Exceptions;

public class BadPlayerException extends Exception
{
	private static final long serialVersionUID = 1L;

	private String message;

	public BadPlayerException()
	{
		this.message = "";
	}

	public BadPlayerException(String message)
	{
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}
}
