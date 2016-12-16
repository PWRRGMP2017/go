package pwrrgmp2017.go.game.GameStates;

import pwrrgmp2017.go.game.Model.GameModel;

public class WhiteTurn extends PlayerTurn
{

	@Override
	public GameState makeMovement(GameModel model)
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
