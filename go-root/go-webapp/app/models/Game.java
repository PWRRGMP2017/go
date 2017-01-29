package models;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import models.msgs.Movement;
import models.msgs.Passing;
import models.msgs.Resignation;
import models.msgs.TerritoryAcceptation;
import pwrrgmp2017.go.game.GameController;
import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exception.GameStillInProgressException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.Model.GameBoard.Field;

public class Game extends UntypedActor
{
	private GameController controller;
	private Field[][] territoryBoard;
	private boolean acceptedPreviousTurn;
	private ActorRef blackPlayer, whitePlayer, currentPlayer;


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
	
	private void initializeGame(ActorRef player) throws GameStillInProgressException, BadFieldException
	{
		if (player == blackPlayer)
			controller.initialiseGame(Field.BLACKSTONE);
		else
			controller.initialiseGame(Field.WHITESTONE);
	}
	
	
	private ActorRef getOpponent(ActorRef player)
	{
		return player == blackPlayer ? whitePlayer : blackPlayer;
	}
	
	private void resign() throws GameIsEndedException
	{
		controller.resign();
	}
	
	private void pass(ActorRef player) throws GameBegginsException, GameIsEndedException, BadFieldException
	{
		if (player == blackPlayer)
			controller.pass(Field.BLACKSTONE);
		else if (player == whitePlayer)
			controller.pass(Field.WHITESTONE);
	}
	
	private void changeTerritory(int x, int y)
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
	
	private void refreshTerritory()
	{
		this.territoryBoard=this.controller.getPossibleTerritory();
	}

	@Override
	public void onReceive(Object message) throws Exception
	{
		if (message instanceof Movement)
		{
			onMovement((Movement) message);
		}
		if (message instanceof Passing)
		{
			onPassing((Passing) message);
		}
		if (message instanceof Resignation)
		{
			onResignation((Resignation) message);
		}
		if (message instanceof TerritoryAcceptation)
		{
			onTerritoryAcceptation((TerritoryAcceptation) message);
		}
		
	}

	private void onTerritoryAcceptation(TerritoryAcceptation message)
	{
		// TODO Auto-generated method stub
		
	}

	private void onResignation(Resignation message)
	{
		// TODO Auto-generated method stub
		
	}

	private void onPassing(Passing message)
	{
		// TODO Auto-generated method stub
		
	}

	private void onMovement(Movement message)
	{
		// TODO Auto-generated method stub
		
	}
	
}
