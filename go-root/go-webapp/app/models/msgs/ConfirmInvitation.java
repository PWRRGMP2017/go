package models.msgs;

import akka.actor.ActorRef;

public class ConfirmInvitation
{
	public final ActorRef game;

	public ConfirmInvitation(final ActorRef game)
	{
		this.game = game;
	}
}
