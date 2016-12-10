package pwrrgmp2017.go.server;

import pwrrgmp2017.go.game.GameController;
import pwrrgmp2017.go.game.GameStates.GameState;
import pwrrgmp2017.go.server.Exceptions.BadPlayerException;
import pwrrgmp2017.go.server.Exceptions.OverridePlayersException;

public class Game extends Thread
{
	private GameState state;
	private GameController controller;
	private PlayerConnection player1;
	private PlayerConnection player2;
	private boolean[][] possibleMovements;

	Game(GameController controller, String thread)
	{
		super(thread);
		this.controller = controller;
		start();
	}

	// Gdzieś trzeba użyc synchronizacji w przypadku gdy oba playerzy w tym
	// samym momencie będą chcieli dac wiadomośc/zakonczyc program
	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				wait(); // czeka na notify() innego wątku i współpracuje z
						// GameController
				// TODO
			}
			catch (InterruptedException e)
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
		if (player == this.player1)
		{
			isWhite = true;
		}
		else if (player == this.player2)
		{
			isWhite = false;
		}
		else
			throw new BadPlayerException();
		// Zastosowanie wzorca State ze sprawdzeniem możliwości wykonania ruchu
	}

	public void addExitMessage(PlayerConnection player) throws BadPlayerException
	{
		boolean isWhite;
		if (player == this.player1)
		{
			isWhite = true;
		}
		else if (player == this.player2)
		{
			isWhite = false;
		}
		else
			throw new BadPlayerException();
		// Zastosowanie wzorca State
		// Wiadomośc do GamesManager?
	}
	// Co z ponowieniem gry? Ilośc partii z konkretna osoba? Raczej w
	// GameManager nie? Wtedy przyda się lista gier pewnie :D

	public void setPlayers(PlayerConnection player, PlayerConnection opponent) throws OverridePlayersException
	{
		if(this.player1==null || this.player2==null)
			throw new OverridePlayersException();
		
		this.player1=player;
		this.player2=opponent;
	}
	
	public PlayerConnection getPlayerConnection(int player)
	{
		switch(player)
		{
		case 1:
			return player1;
		case 2:
			return player2;
		default:
			return null;
		}
	}
}
