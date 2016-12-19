package pwrrgmp2017.go.game.GameStates;

import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.Model.GameBoard;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.game.Model.GameModel;

public class BlackTurn extends PlayerTurn
{

	public BlackTurn(boolean b)
	{
		super(b);
	}

	@Override
	public GameState makeMovement(GameModel model, int x, int y, Field playerField, GameBoard board) throws BadFieldException, GameBegginsException, GameIsEndedException
	{
		if(playerField==Field.BLACKSTONE)
			board.makeMovement(x, y, playerField, Field.WHITESTONE);
		else
			throw new BadFieldException();
		
		return new WhiteTurn(false);
	}

	@Override
	public GameState pass(GameModel model, Field colour) throws BadFieldException
	{
		if(colour==Field.BLACKSTONE)
		{
			if(super.pass==true)
				return new EndState();
			else
				return new WhiteTurn(true);
		}
		
		throw new BadFieldException();
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
