package pwrrgmp2017.go.game.GameStates;

import pwrrgmp2017.go.game.Model.GameModel;

public interface GameState
{
	
	GameState makeMovement(GameModel model);
	
	GameState pass(GameModel model);
	
	GameState initialiseGame(GameModel model);
	
	GameState resign(GameModel model);
	
	GameStateEnum getState();
	
}
