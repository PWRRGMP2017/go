package pwrrgmp2017.go.game.GameStates;

import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Model.GameBoard;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.game.Model.GameModel;

public class EndState implements GameState
{

	@Override
	public GameState makeMovement(GameModel model, int x, int y, Field playerField, GameBoard board) throws GameIsEndedException
	{
		throw new GameIsEndedException();
	}

	@Override
	public GameState pass(GameModel model, Field colour) throws GameIsEndedException
	{
		throw new GameIsEndedException();
	}

	@Override
	public GameState initialiseGame(GameModel model)
	{
		return new BeginningState();
	}

	@Override
	public GameState resign(GameModel model) throws GameIsEndedException
	{
		throw new GameIsEndedException();
	}

	@Override
	public GameStateEnum getState()
	{
		return GameStateEnum.END;
	}
}
