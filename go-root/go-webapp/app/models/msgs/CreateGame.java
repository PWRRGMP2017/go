package models.msgs;

import akka.actor.ActorRef;

public class CreateGame
{
	public final ActorRef game;
	public final boolean isBlack;
	
	public CreateGame(ActorRef game, boolean isBlack)
	{
		this.game=game;
		this.isBlack=isBlack;
	}
}
