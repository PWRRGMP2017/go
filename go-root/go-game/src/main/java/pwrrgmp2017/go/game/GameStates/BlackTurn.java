package pwrrgmp2017.go.game.GameStates;

import pwrrgmp2017.go.game.Model.GameModel;

public class BlackTurn extends PlayerTurn
{

	@Override
	public GameState makeMovement(GameModel model)
	{
		return new WhiteTurn();
		// TODO
		
	}

	@Override
	public GameState pass(GameModel model)
	{
		return new WhiteTurn();
		
	}


	@Override
	public GameState resign(GameModel model)
	{
		return new EndState();
		
	}

	@Override
	public GameStateEnum getState()
	{
		return GameStateEnum.BLACKMOVE;
	}
}
