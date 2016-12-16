package pwrrgmp2017.go.game.GameStates;

public class WhiteTurn extends PlayerTurn
{

	@Override
	public GameState makeMovement()
	{
		// TODO Auto-generated method stub
		return new BlackTurn();
	}

	@Override
	public GameState pass()
	{
		// TODO Auto-generated method stub
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
		return GameStateEnum.WHITEMOVE;
	}
}
