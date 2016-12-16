package pwrrgmp2017.go.game.GameStates;

public class BlackTurn extends PlayerTurn
{

	@Override
	public GameState makeMovement()
	{
		return new WhiteTurn();
		// TODO
		
	}

	@Override
	public GameState pass()
	{
		return new WhiteTurn();
		
	}


	@Override
	public GameState resign()
	{
		return new EndState();
		
	}

	@Override
	public GameStateEnum getState()
	{
		return GameStateEnum.BLACKMOVE;
	}
}
