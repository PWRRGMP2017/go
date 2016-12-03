package pwrrgmp2017.go.server;

import pwrrgmp2017.go.server.Exceptions.BadPlayerException;
import pwrrgmp2017.go.server.GameStates.GameState;
import pwrrgmp2017.go.server.Model.GameModel;

public class Game extends Thread
{
	GameState state;
	ControllerGame controller;
	PlayerConnection player1;
	PlayerConnection player2;
	boolean[][] possibleMovements;

	Game(GameModel model, String thread)
	{
		super(thread);
		controller= new ControllerGame(model, this);
		start();
	}

	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				wait();
				//TODO
			}
			catch(InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean isTurnInGoodPlace()
	{
		//TODO
		return false;
	}

	void addMessageFromPlayer(PlayerConnection player, String msg) throws BadPlayerException
	{
		if(player==this.player1)
		{
		}
		else
			if(player==this.player2)
			{
			}
			else
				throw new BadPlayerException();
	}
}
