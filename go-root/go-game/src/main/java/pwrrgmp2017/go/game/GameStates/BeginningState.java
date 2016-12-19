package pwrrgmp2017.go.game.GameStates;

import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Model.GameBoard;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.game.Model.GameModel;

public class BeginningState implements GameState
{

	@Override
	public GameState makeMovement(GameModel model, int x, int y, Field playerField, GameBoard board) throws GameBegginsException
	{
		throw new GameBegginsException();
	}

	@Override
	public GameState pass(GameModel model, Field colour) throws GameBegginsException
	{
		throw new GameBegginsException();
	}

	@Override
	public GameState initialiseGame(GameModel model)
	{
		//TODO
		return new BlackTurn(false);
	}

	@Override
	public GameState resign(GameModel model)
	{
		return new EndState();
	}

	@Override
	public GameStateEnum getState()
	{
		return GameStateEnum.BEGINNING;
	}
}
