package pwrrgmp2017.go.game.GameStates;

public interface GameState
{
	
	abstract GameState makeMovement();
	
	abstract GameState pass();
	
	abstract GameState initialiseGame();
	
	abstract GameState resign();
	
	abstract GameStateEnum getState();
	
}
