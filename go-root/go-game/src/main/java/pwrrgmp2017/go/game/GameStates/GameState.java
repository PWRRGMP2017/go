package pwrrgmp2017.go.game.GameStates;

import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exception.GameStillInProgressException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.Model.GameBoard;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.game.Model.GameModel;

public interface GameState
{
	
	GameState makeMovement(GameModel model, int x, int y, Field playerField, GameBoard board) throws BadFieldException, GameBegginsException, GameIsEndedException;
	
	GameState pass(GameModel model) throws GameBegginsException, GameIsEndedException;
	
	GameState initialiseGame(GameModel model) throws GameStillInProgressException;
	
	GameState resign(GameModel model) throws GameIsEndedException;
	
	GameStateEnum getState();
	
}
