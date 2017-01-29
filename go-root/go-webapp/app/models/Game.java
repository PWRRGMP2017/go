package models;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import models.msgs.AcceptTerritory;
import models.msgs.Move;
import models.msgs.Pass;
import models.msgs.Resign;
import models.msgs.ResumeGame;
import pwrrgmp2017.go.game.GameController;
import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exception.GameStillInProgressException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.Model.GameBoard.Field;

public class Game extends UntypedActor
{
	protected GameController controller;
	protected Field[][] territoryBoard;
	protected boolean acceptedPreviousTurn;
	protected ActorRef blackPlayer, whitePlayer, currentPlayer;


	Game(ActorRef blackPlayer, ActorRef whitePlayer, GameController controller)
	{
		this.controller = controller;
		this.blackPlayer = blackPlayer;
		this.whitePlayer = whitePlayer;
		this.currentPlayer = blackPlayer;

		try
		{
			controller.initialiseGame(Field.BLACKSTONE);
		}
		catch (GameStillInProgressException e)
		{
			// Should not happen
			e.printStackTrace();
		}
		catch (BadFieldException e)
		{
			e.printStackTrace();
		}
	}
	
	protected void initializeGame(ActorRef player) throws GameStillInProgressException, BadFieldException
	{
		if (player == blackPlayer)
			controller.initialiseGame(Field.BLACKSTONE);
		else
			controller.initialiseGame(Field.WHITESTONE);
	}
	
	
	protected ActorRef getOpponent(ActorRef player)
	{
		return player == blackPlayer ? whitePlayer : blackPlayer;
	}
	
	protected void resign() throws GameIsEndedException
	{
		controller.resign();
	}
	
	protected void pass(ActorRef player) throws GameBegginsException, GameIsEndedException, BadFieldException
	{
		if (player == blackPlayer)
			controller.pass(Field.BLACKSTONE);
		else if (player == whitePlayer)
			controller.pass(Field.WHITESTONE);
	}
	
	protected void changeTerritory(int x, int y)
	{
		switch (territoryBoard[x][y])
		{
		case BLACKSTONE:
			territoryBoard[x][y]=Field.DEADBLACK;
			break;
		case BLACKTERRITORY:
			territoryBoard[x][y]=Field.WHITETERRITORY;
			break;
		case DEADBLACK:
			territoryBoard[x][y]=Field.BLACKSTONE;
			break;
		case DEADWHITE:
			territoryBoard[x][y]=Field.WHITESTONE;
			break;
		case EMPTY:
			territoryBoard[x][y]=Field.BLACKTERRITORY;
			break;
		case NONETERRITORY:
			territoryBoard[x][y]=Field.BLACKTERRITORY;
			break;
		case WALL:
			territoryBoard[x][y]=Field.WALL;
			break;
		case WHITESTONE:
			territoryBoard[x][y]=Field.DEADWHITE;
			break;
		case WHITETERRITORY:
			territoryBoard[x][y]=Field.NONETERRITORY;
			break;
		default:
			break;
		}
	}
	
	protected void refreshTerritory()
	{
		this.territoryBoard=this.controller.getPossibleTerritory();
	}

	@Override
	public void onReceive(Object message) throws Exception
	{
		if (message instanceof Move)
		{
			onMove((Move) message);
		}
		else if (message instanceof Pass)
		{
			onPass((Pass) message);
		}
		else if (message instanceof Resign)
		{
			onResign((Resign) message);
		}
		else if (message instanceof AcceptTerritory)
		{
			onAcceptTerritory((AcceptTerritory) message);
		}
		else if (message instanceof ResumeGame) {
			onResumeGame((ResumeGame) message);
		}
		else
		{
			unhandled(message);
		}
		
	}

	protected void onResumeGame(ResumeGame message)
	{
		// TODO Auto-generated method stub
		
	}

	protected void onAcceptTerritory(AcceptTerritory message)
	{
		// TODO Auto-generated method stub
		
	}

	protected void onResign(Resign message)
	{
		// TODO Auto-generated method stub
		
	}

	protected void onPass(Pass message)
	{
		// TODO Auto-generated method stub
		
	}

	private void onMove(Move message)
	{
		// TODO Auto-generated method stub
		
	}
	
}
