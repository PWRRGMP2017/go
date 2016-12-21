package pwrrgmp2017.go.game.Model;

import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exception.GameStillInProgressException;
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
	
	public void initialiseGame(Field firstPlayer) throws GameStillInProgressException, BadFieldException
	{
		this.state=state.initialiseGame(this, firstPlayer);
	}
	
	public void pass(Field colour) throws GameBegginsException, GameIsEndedException, BadFieldException
	{
		this.state=state.pass(this, colour);
	}
	
	public void resign() throws GameIsEndedException
	{
		this.state=state.resign(this);
	}
	
	public abstract float calculateScore();
	
	public abstract float calculateScore(Field[][] territory);
	
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
			if(possibleMovementsWhite==null)
			{
				try
				{
					possibleMovementsWhite=board.getPossibleMovements(Field.WHITESTONE);
				}
				catch (BadFieldException e) {} //nigdy się nie wykona
			}
			return possibleMovementsWhite.clone();
		}
		else
			throw new BadFieldException();
	}

	public void addMovement(int x, int y, Field playerField) throws BadFieldException, GameBegginsException, GameIsEndedException 
	{
		this.state=this.state.makeMovement(this, x, y, playerField, board);
		
		possibleMovementsBlack=null;
		possibleMovementsWhite=null;
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
	
	public int getBlackCaptives()
	{
		return board.getBlackCaptives();
	}
	
	public int getWhiteCaptives()
	{
		return board.getWhiteCaptives();
	}
	
}
