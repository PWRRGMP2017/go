package models;

import pwrrgmp2017.go.game.GameController;
import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exception.GameStillInProgressException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.Model.GameBoard.Field;

public class Game
{
	private GameController controller;
	private Field[][] territoryBoard;
	private boolean acceptedPreviousTurn;
	private Player blackPlayer, whitePlayer, currentPlayer;


	Game(Player blackPlayer, Player whitePlayer, GameController controller)
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
	
	public Player getBlackPlayer()
	{
		return blackPlayer;
	}
	
	public Player getWhitePlayer()
	{
		return whitePlayer;
	}
	
	private void initializeGame(Player player) throws GameStillInProgressException, BadFieldException
	{
		if (player == blackPlayer)
			controller.initialiseGame(Field.BLACKSTONE);
		else
			controller.initialiseGame(Field.WHITESTONE);
	}
	
	
	private Player getOpponent(Player player)
	{
		return player == blackPlayer ? whitePlayer : blackPlayer;
	}
	
	private void resign() throws GameIsEndedException
	{
		controller.resign();
	}
	
	private void pass(Player player) throws GameBegginsException, GameIsEndedException, BadFieldException
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
	
}
