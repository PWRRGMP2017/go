package pwrrgmp2017.go.game.Model;

import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.GameStates.BeginningState;
import pwrrgmp2017.go.game.GameStates.GameState;
import pwrrgmp2017.go.game.GameStates.GameStateEnum;
import pwrrgmp2017.go.game.Model.GameBoard.Field;

public abstract class GameModel
{
	private GameBoard board;
	private boolean[][] possibleMovementsWhite;
	private boolean[][] possibleMovementsBlack;
	private GameState state;
	private float komi;

	GameModel(GameBoard board, float komi)
	{
		this.board=board;
		this.komi=komi;
		state=new BeginningState();
	}
	
	public void initialiseGame()
	{
		state.initialiseGame(this);
	}
	
	public void pass()
	{
		state.pass(this);
	}
	
	public void resign()
	{
		state.resign(this);
	}
	
	public abstract float calculateScore();
	
	public abstract Field[][] getPossibleTerritory();
	
	public boolean[][] getPossibleMovements(Field colour) throws BadFieldException
	{
		if (colour.equals(Field.BLACKSTONE))
		{
			if(possibleMovementsBlack==null)
			{
				try
				{
					possibleMovementsBlack=board.getPossibleMovements(Field.BLACKSTONE);
				}
				catch (BadFieldException e) {} //nigdy się nie wykona
			}
			return possibleMovementsBlack.clone();
		}
		else if (colour.equals(Field.WHITESTONE))
		{
			if(possibleMovementsBlack==null)
			{
				try
				{
					possibleMovementsBlack=board.getPossibleMovements(Field.WHITESTONE);
				}
				catch (BadFieldException e) {} //nigdy się nie wykona
			}
			return possibleMovementsWhite.clone();
		}
		else
			throw new BadFieldException();
	}

	public boolean addMovement(int x, int y, Field playerField)
	{
		try
		{
			this.state=this.state.makeMovement(this, x, y, playerField, board);
		}
		catch (BadFieldException e)
		{
			return false;
		}
		possibleMovementsBlack=null;
		possibleMovementsWhite=null;
		return true;
	}

	public boolean isTurnPossible(int x, int y, Field colour) throws BadFieldException
	{
		if (colour==Field.BLACKSTONE)
		{
			if(possibleMovementsBlack==null)
			{
				try
				{
					possibleMovementsBlack=board.getPossibleMovements(Field.BLACKSTONE);
				}
				catch (BadFieldException e) {} //nigdy się nie wykona
			}
			return possibleMovementsBlack[x][y];
		}
		else if (colour==Field.WHITESTONE)
		{
			if(possibleMovementsBlack==null)
			{
				try
				{
					possibleMovementsBlack=board.getPossibleMovements(Field.WHITESTONE);
				}
				catch (BadFieldException e) {} //nigdy się nie wykona
			}
			return possibleMovementsWhite[x][y];
		}
		else
			throw new BadFieldException();
	}

	public GameStateEnum getState()
	{
		return state.getState();
	}
	
	public Field[][] getBoardCopy()
	{
		return board.getBoardCopy();
	}
	
	public float getKomi()
	{
		return komi;
	}
}
