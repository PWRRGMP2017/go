package pwrrgmp2017.go.game.GameStates;

import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.Model.GameBoard;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.game.Model.GameModel;

/**
 * Class which represents state of turn of white Player
 * @author Robert Gawlik
 *
 */
public class WhiteTurn extends PlayerTurn
{
	
	public WhiteTurn(boolean b)
	{
		super(b);
	}

	@Override
	public GameState makeMovement(GameModel model, int x, int y, Field playerField, GameBoard board) throws BadFieldException, GameBegginsException, GameIsEndedException
	{
		if(playerField==Field.WHITESTONE)
		{
			if(!board.makeMovement(x, y, playerField, Field.BLACKSTONE))
			{
				throw new BadFieldException();
			}
		}
		else
			throw new BadFieldException();
		
		return new BlackTurn(false);
	}

	@Override
	public GameState pass(GameModel model, Field colour) throws BadFieldException
	{
		if(colour==Field.WHITESTONE)
		{
			if(super.pass==true)
				return new EndState();
			else
				return new BlackTurn(true);
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
		return GameStateEnum.WHITEMOVE;
	}
}
