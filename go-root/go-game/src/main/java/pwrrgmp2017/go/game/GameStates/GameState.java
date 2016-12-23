package pwrrgmp2017.go.game.GameStates;

import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exception.GameStillInProgressException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.Model.GameBoard;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.game.Model.GameModel;

/**
 * Class which represents state of the game. STATE
 */
public interface GameState
{
	
	/**
	 * Adds movement on the board
	 * @param x first attribute position
	 * @param y second attribute position
	 * @param playerField
	 * @throws BadFieldException
	 * @throws GameBegginsException
	 * @throws GameIsEndedException
	 */
	GameState makeMovement(GameModel model, int x, int y, Field playerField, GameBoard board) throws BadFieldException, GameBegginsException, GameIsEndedException;
	
	/**
	 * Passing by the player
	 * @param colour Colour of player
	 * @throws GameBegginsException Bad state
	 * @throws GameIsEndedException Bad state
	 * @throws BadFieldException Field isn't represented stone
	 */
	GameState pass(GameModel model, Field colour) throws GameBegginsException, GameIsEndedException, BadFieldException;
	
	/**
	 * Makes game ready for play
	 * @param firstPlayer Player who should begin
	 * @throws GameStillInProgressException 
	 * @throws BadFieldException
	 */
	GameState initialiseGame(GameModel model, Field firstPlayer) throws GameStillInProgressException, BadFieldException;
	
	/**
	 * Resigning by the player
	 * @throws GameIsEndedException Bad state
	 */
	GameState resign(GameModel model) throws GameIsEndedException;
	
	/**
	 * Returns state in enum 
	 * @return state
	 */
	GameStateEnum getState();
	
}
