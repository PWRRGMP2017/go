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
	private boolean[][] possibleMovements;

	Game(GameModel model, String thread)
	{
		super(thread);
		controller= new ControllerGame(model, this);
		start();
	}

	//Gdzieś trzeba użyc synchronizacji w przypadku gdy oba playerzy w tym samym momencie będą chcieli dac wiadomośc/zakonczyc program
	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				wait(); //czeka na notify() innego wątku i współpracuje z GameController
				//TODO 
			}
			catch(InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean isTurnInGoodPlace(int lenght, int high)
	{
		return possibleMovements[high][lenght];
	}

	public void addTurnMessage(PlayerConnection player, String msg) throws BadPlayerException
	{
		boolean isWhite;
		if(player==this.player1)
		{
			isWhite= true;
		}
		else
			if(player==this.player2)
			{
				isWhite= false;
			}
			else
				throw new BadPlayerException();
		//Zastosowanie wzorca State ze sprawdzeniem możliwości wykonania ruchu
	}

	public void addExitMessage(PlayerConnection player) throws BadPlayerException
	{
		boolean isWhite;
		if(player==this.player1)
		{
			isWhite= true;
		}
		else
			if(player==this.player2)
			{
				isWhite= false;
			}
			else
				throw new BadPlayerException();
		//Zastosowanie wzorca State
		//Wiadomośc do GamesManager?
	}
	//Co z ponowieniem gry? Ilośc partii z konkretna osoba? Raczej w GameManager nie? Wtedy przyda się lista gier pewnie :D
}
