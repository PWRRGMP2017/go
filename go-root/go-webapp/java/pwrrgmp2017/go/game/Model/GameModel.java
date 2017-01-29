package pwrrgmp2017.go.game.Model;

import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exception.GameStillInProgressException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.GameStates.BeginningState;
import pwrrgmp2017.go.game.GameStates.GameState;
import pwrrgmp2017.go.game.GameStates.GameStateEnum;
import pwrrgmp2017.go.game.Model.GameBoard.Field;

/**
 * Class which represents model of GO game
 * @author Robert Gawlik
 *
 */
public abstract class GameModel
{
	/**Board of the game */
	private GameBoard board;
	/**Possible movements of White Player */
	private boolean[][] possibleMovementsWhite;
	/**Possible movements of black Player */
	private boolean[][] possibleMovementsBlack;
	/**Current state of the game */
	private GameState state;
	/**Value of comi */
	private float komi;

	/**
	 * Constructor of the model
	 * @param board game's board
	 * @param komi Value of komi
	 */
	GameModel(GameBoard board, float komi)
	{
		this.board=board;
		this.komi=komi;
		state=new BeginningState();
	}
	
	/**
	 * Makes game ready for play
	 * @param firstPlayer Player who should begin
	 * @throws GameStillInProgressException 
	 * @throws BadFieldException
	 */
	public void initialiseGame(Field firstPlayer) throws GameStillInProgressException, BadFieldException
	{
		this.state=state.initialiseGame(this, firstPlayer);
	}
	
	/**
	 * Passing by the player
	 * @param colour Colour of player
	 * @throws GameBegginsException Bad state
	 * @throws GameIsEndedException Bad state
	 * @throws BadFieldException Field isn't represented stone
	 */
	public void pass(Field colour) throws GameBegginsException, GameIsEndedException, BadFieldException
	{
		this.state=state.pass(this, colour);
	}
	
	/**
	 * Resigning by the player
	 * @throws GameIsEndedException Bad state
	 */
	public void resign() throws GameIsEndedException
	{
		this.state=state.resign(this);
	}
	
	/**
	 * Method which calculates the score of current situation
	 */
	public abstract float calculateScore();
	
	/**
	 * Method which calculates score of situation in parametre
	 */
	public abstract float calculateScore(Field[][] territory);
	
	/**
	 * Method which returns possible territory
	 */
	public abstract Field[][] getPossibleTerritory();
	
	/**
	 * Method which returns possible movements
	 * @param colour Colour of player
	 * @return
	 * @throws BadFieldException
	 */
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

	/**
	 * Adds movement on the board
	 * @param x first attribute position
	 * @param y second attribute position
	 * @param playerField
	 * @throws BadFieldException
	 * @throws GameBegginsException
	 * @throws GameIsEndedException
	 */
	public void addMovement(int x, int y, Field playerField) throws BadFieldException, GameBegginsException, GameIsEndedException 
	{
		this.state=this.state.makeMovement(this, x, y, playerField, this.board);
		
		possibleMovementsBlack=null;
		possibleMovementsWhite=null;
	}

	/**
	 * Shows if the move possible
	 * @param x first attribute position
	 * @param y second attribute position
	 * @param colour Colour of player
	 * @return True if move is possible
	 * @throws BadFieldException
	 */
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
			if(possibleMovementsWhite==null)
			{
				try
				{
					possibleMovementsWhite=board.getPossibleMovements(Field.WHITESTONE);
				}
				catch (BadFieldException e) {} //nigdy się nie wykona
			}
			return possibleMovementsWhite[x][y];
		}
		else
			throw new BadFieldException();
	}

	/**
	 * Returns state in enum 
	 * @return state
	 */
	public GameStateEnum getState()
	{
		return state.getState();
	}
	
	/**
	 * Returns copied board
	 * @return copied array as a board
	 */
	public Field[][] getBoardCopy()
	{
		return board.getBoardCopy();
	}
	
	/**
	 * Getter of komi
	 * @return Value of komi
	 */
	public float getKomi()
	{
		return komi;
	}
	
	/**
	 * Getter of black captives
	 * @return number of black captives
	 */
	public int getBlackCaptives()
	{
		return board.getBlackCaptives();
	}
	
	/**
	 * Getter of white captives
	 * @return number of white captives
	 */
	public int getWhiteCaptives()
	{
		return board.getWhiteCaptives();
	}
	
}
