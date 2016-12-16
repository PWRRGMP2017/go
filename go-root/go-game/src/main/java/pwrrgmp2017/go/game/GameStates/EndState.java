package pwrrgmp2017.go.game.GameStates;

public class EndState implements GameState
{

	@Override
	public GameState makeMovement()
	{
		return this;
	}

	@Override
	public GameState pass()
	{
		return this;
	}

	@Override
	public GameState initialiseGame()
	{
		return new BeginningState();
		
	}

	@Override
	public GameState resign()
	{
		return this;
	}

	@Override
	public GameStateEnum getState()
	{
		return GameStateEnum.END;
	}
}
