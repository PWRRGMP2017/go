package models.msgs;

import akka.actor.ActorRef;
import pwrrgmp2017.go.game.factory.GameInfo;

public class Invite
{
	public final String invitingPlayerName;
	public final String invitedPlayerName;
	public final GameInfo gameInfo;
	public final ActorRef invitingPlayer;
	public final ActorRef invitedPlayer;

	public Invite(final String invitingPlayerName, final String invitedPlayerName, final ActorRef invitingPlayer,
			final ActorRef invitedPlayer, final GameInfo gameInfo)
	{
		this.invitingPlayerName = invitingPlayerName;
		this.invitedPlayerName = invitedPlayerName;
		this.invitedPlayer = invitedPlayer;
		this.invitingPlayer = invitingPlayer;
		this.gameInfo = gameInfo;
	}
}
