package pwrrgmp2017.go.game;

import java.awt.Point;

import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exception.GameStillInProgressException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.GameStates.GameStateEnum;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.game.Model.GameModel;

/**
 * Class which helps in controlling model of the game
 * and implementation of bot
 * @author Robert Gawlik
 *
 */
public class GameController
{
	/**model of the game */
	private GameModel model;
	/**First position attribute of last move */
	protected int lastMoveX;
	/**Second position attribute of last move */
	protected int lastMoveY;

	/**
	 * Constructor of the class
	 * @param model model which we want to control
	 */
	public GameController(GameModel model)
	{
		this.model = model;
	}

	/**
	 * Method which returns possible movements
	 * @param colour Colour of player
	 * @return
	 * @throws BadFieldException
	 */
	public boolean[][] getPossibleMovements(Field colour) throws BadFieldException
	{
		return model.getPossibleMovements(colour);
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
		model.addMovement(x, y, playerField);
		
		lastMoveX=x;
		lastMoveY=y;
	}

	/**
	 * Shows if the move possible
	 * @param x first attribute position
	 * @param y second attribute position
	 * @param colour Colour of player
	 * @return True if move is possible
	 * @throws BadFieldException
	 */
	boolean isTurnPossible(int x, int y, Field playerField) throws BadFieldException
	{
		return model.isTurnPossible(x, y, playerField);
	}
	
	/**
	 * Method which calculates the score of current situation
	 */
	public float calculateScore()
	{
		return model.calculateScore();
	}
	
	/**
	 * Method which calculates score of situation in parametre
	 */
	public float calculateScore(Field[][] territory)
	{
		return model.calculateScore(territory);
	}

	/**
	 * Method which returns possible territory
	 */
	public Field[][] getPossibleTerritory()
	{
		return model.getPossibleTerritory();
	}
	
	/**
	 * Returns state in enum 
	 * @return state
	 */
	public GameStateEnum getState()
	{
		return model.getState();
	}
	
	/**
	 * Returns copied board
	 * @return copied array as a board
	 */
	public Field[][] getBoardCopy()
	{
		return model.getBoardCopy();
	}
	
	/**
	 * Getter of komi
	 * @return Value of komi
	 */
	public float getKomi()
	{
		return model.getKomi();
	}
	
	/**
	 * Makes game ready for play
	 * @param firstPlayer Player who should begin
	 * @throws GameStillInProgressException 
	 * @throws BadFieldException
	 */
	public void initialiseGame(Field firstPlayer) throws GameStillInProgressException, BadFieldException 
	{
		model.initialiseGame(firstPlayer);
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
		model.pass(colour);
	}
	
	/**
	 * Resigning by the player
	 * @throws GameIsEndedException Bad state
	 */
	public void resign() throws GameIsEndedException
	{
		model.resign();
	}
	
	/**
	 * Returns last movement
	 * @return last movement
	 */
	public Point getLastMovement()
	{
		return new Point( lastMoveX, lastMoveY);
	}
	
	/**
	 * Getter of white captives
	 * @return number of white captives
	 */
	public int getBlackCaptives()
	{
		return model.getBlackCaptives();
	}
	
	/**
	 * Getter of black captives
	 * @return number of black captives
	 */
	public int getWhiteCaptives()
	{
		return model.getWhiteCaptives();
	}
}
