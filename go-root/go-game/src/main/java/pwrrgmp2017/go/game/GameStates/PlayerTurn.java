package pwrrgmp2017.go.game.GameStates;

public abstract class PlayerTurn implements GameState
{
	@Override
	public GameState initialiseGame()
	{
		return this;
	}
}
