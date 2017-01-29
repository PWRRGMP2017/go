package pwrrgmp2017.go.game.GameStates;

import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.Model.GameBoard;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.game.Model.GameModel;

/**
 * Class which represents state of end of the game
 * @author Robert Gawlik
 *
 */
public class EndState implements GameState
{

	@Override
	public GameState makeMovement(GameModel model, int x, int y, Field playerField, GameBoard board) throws GameIsEndedException
	{
		throw new GameIsEndedException();
	}

	@Override
	public GameState pass(GameModel model, Field colour) throws GameIsEndedException
	{
		throw new GameIsEndedException();
	}

	@Override
	public GameState initialiseGame(GameModel model, Field firstPlayer) throws BadFieldException
	{
		if(firstPlayer==Field.BLACKSTONE)
			return new BlackTurn(false);
		else if(firstPlayer==Field.WHITESTONE)
			return new WhiteTurn(false);
		else throw new BadFieldException();
	}

	@Override
	public GameState resign(GameModel model) throws GameIsEndedException
	{
		throw new GameIsEndedException();
	}

	@Override
	public GameStateEnum getState()
	{
		return GameStateEnum.END;
	}
}
