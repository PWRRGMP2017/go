package models.msgs;

import akka.actor.ActorRef;

public class WaitingPlayer
{
	final public ActorRef player;
	final public String name;
	public WaitingPlayer(ActorRef player, String name)
	{
		this.player=player;
		this.name=name;
	}
}
