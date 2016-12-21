package pwrrgmp2017.go.game.GameStates;

import pwrrgmp2017.go.game.Exception.GameStillInProgressException;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.game.Model.GameModel;

public abstract class PlayerTurn implements GameState
{
	protected boolean pass;
	public PlayerTurn()
	{
		pass=false;
	}
	
	public PlayerTurn(boolean pass)
	{
		this.pass=pass;
	}
	
	@Override
	public GameState initialiseGame(GameModel model, Field firstPlayer) throws GameStillInProgressException
	{
		throw new GameStillInProgressException();
	}
}
