package models.msgs;

public class RespondToInvitation
{
	public final boolean isAccepted;
	public final String reason;

	public RespondToInvitation(final boolean isAccepted, final String reason)
	{
		this.isAccepted = isAccepted;
		this.reason = reason;
	}
}
