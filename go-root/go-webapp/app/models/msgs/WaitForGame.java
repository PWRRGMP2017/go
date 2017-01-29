package models.msgs;

import akka.actor.ActorRef;
import pwrrgmp2017.go.game.factory.GameInfo;

public class WaitForGame
{
	public final ActorRef player;
	public final GameInfo gameInfo;

	public WaitForGame(ActorRef player, GameInfo gameInfo)
	{
		this.player=player;
		this.gameInfo=gameInfo;
	}
	
}
