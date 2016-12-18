package pwrrgmp2017.go.game.GameStates;

import pwrrgmp2017.go.game.Model.GameBoard;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.game.Model.GameModel;

public class EndState implements GameState
{

	@Override
	public GameState makeMovement(GameModel model, int x, int y, Field playerField, GameBoard board)
	{
		return this;
	}

	@Override
	public GameState pass(GameModel model)
	{
		return this;
	}

	@Override
	public GameState initialiseGame(GameModel model)
	{
		return new BeginningState();
		
	}

	@Override
	public GameState resign(GameModel model)
	{
		return this;
	}

	@Override
	public GameStateEnum getState()
	{
		return GameStateEnum.END;
	}
}
