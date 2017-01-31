package models.msgs;

import akka.actor.ActorRef;

public class CreateGame
{
	public final ActorRef game;
	public final boolean isBlack;
	public final String opponentName;
	
	public CreateGame(ActorRef game, boolean isBlack, String opponentName)
	{
		this.game=game;
		this.isBlack=isBlack;
		this.opponentName=opponentName;
	}
}
