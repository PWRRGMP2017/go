package pwrrgmp2017.go.game.GameStates;

import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.Model.GameBoard;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.game.Model.GameModel;

public interface GameState
{
	
	GameState makeMovement(GameModel model, int x, int y, Field playerField, GameBoard board) throws BadFieldException;
	
	GameState pass(GameModel model);
	
	GameState initialiseGame(GameModel model);
	
	GameState resign(GameModel model);
	
	GameStateEnum getState();
	
}
