package pwrrgmp2017.go.game.GameStates;

import pwrrgmp2017.go.game.Model.GameBoard;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.game.Model.GameModel;

public class WhiteTurn extends PlayerTurn
{

	@Override
	public GameState makeMovement(GameModel model, int x, int y, Field playerField, GameBoard board)
	{
		// TODO Auto-generated method stub
		return new BlackTurn();
	}

	@Override
	public GameState pass(GameModel model)
	{
		// TODO Auto-generated method stub
		return new BlackTurn();
	}


	@Override
	public GameState resign(GameModel model)
	{
		return new EndState();
		
	}

	@Override
	public GameStateEnum getState()
	{
		return GameStateEnum.WHITEMOVE;
	}
}
