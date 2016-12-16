package pwrrgmp2017.go.game.GameStates;

import pwrrgmp2017.go.game.Model.GameModel;

public abstract class PlayerTurn implements GameState
{
	@Override
	public GameState initialiseGame(GameModel model)
	{
		return this;
	}
}
