package models;

import akka.actor.UntypedActor;
import models.msgs.GetPlayerName;

public class BotPlayer extends UntypedActor
{
	public BotPlayer()
	{
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
		getSender().tell("Bot", getSelf()); 
	}

}
