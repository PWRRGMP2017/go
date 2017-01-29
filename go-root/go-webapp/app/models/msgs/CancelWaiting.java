package models.msgs;

import akka.actor.ActorRef;

public class CancelWaiting
{
	public final ActorRef player;
	public final String gameInfo;

	public CancelWaiting(ActorRef player, String gameInfo)
	{
		this.player=player;
		this.gameInfo=gameInfo;
	}
}
