package models.msgs;

import akka.actor.ActorRef;
import pwrrgmp2017.go.game.factory.GameInfo;

public class PlayBotGame
{
	public final GameInfo gameInfo;
	public final ActorRef player;

	public PlayBotGame(GameInfo gameInfo, ActorRef player)
	{
		this.gameInfo=gameInfo;
		this.player=player;
	}
}
