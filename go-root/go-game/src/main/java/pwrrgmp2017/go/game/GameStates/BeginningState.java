package pwrrgmp2017.go.game.GameStates;

public class BeginningState implements GameState
{

	@Override
	public GameState makeMovement()
	{
		//TODO
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
		//TODO
		return new BlackTurn();
	}

	@Override
	public GameState resign()
	{
		return new EndState();
	}

	@Override
	public GameStateEnum getState()
	{
		return GameStateEnum.BEGINNING;
	}
}
