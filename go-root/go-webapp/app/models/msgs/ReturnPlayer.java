package models.msgs;

import akka.actor.ActorRef;

public class ReturnPlayer
{
	public final ActorRef player;

	public ReturnPlayer(final ActorRef player)
	{
		this.player = player;
	}
}
