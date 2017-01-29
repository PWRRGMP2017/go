package models;

import akka.actor.ActorRef;
import models.msgs.AcceptTerritory;
import models.msgs.Pass;
import models.msgs.Resign;
import models.msgs.ResumeGame;
import pwrrgmp2017.go.game.GameController;

public class BotGame extends Game
{
	public BotGame(ActorRef blackPlayer, GameController controller)
	{
		super(blackPlayer, null, controller);
	}
	
	@Override
	public void onReceive(Object message) throws Exception
	{
		// TODO Auto-generated method stub
		super.onReceive(message);
	}
	
	@Override
	protected void onAcceptTerritory(AcceptTerritory message)
	{
		// TODO Auto-generated method stub
		super.onAcceptTerritory(message);
	}
	
	@Override
	protected void onPass(Pass message)
	{
		// TODO Auto-generated method stub
		super.onPass(message);
	}
	
	@Override
	protected void onResign(Resign message)
	{
		// TODO Auto-generated method stub
		super.onResign(message);
	}
	
	@Override
	protected void onResumeGame(ResumeGame message)
	{
		// TODO Auto-generated method stub
		super.onResumeGame(message);
	}
	
	
}
