package models;

import akka.actor.UntypedActor;
import models.msgs.GetPlayerName;

public class BotPlayer extends UntypedActor
{
	private static int botNumber = 0;
	private int thisBotNumber;
	
	public BotPlayer()
	{
		thisBotNumber = botNumber;
		botNumber += 1;
	}

	@Override
	public void onReceive(Object message) throws Exception
	{
		if (message instanceof GetPlayerName)
		{
			onGetPlayerName((GetPlayerName) message);
		}
		return;
	}

	private void onGetPlayerName(GetPlayerName message)
	{
		getSender().tell("Bot" + thisBotNumber, getSelf());
	}
}
