package pwrrgmp2017.go.game.Model;

import pwrrgmp2017.go.game.Model.GameBoard.Field;

public class ChineseGameModel extends GameModel
{

	public ChineseGameModel(GameBoard board, float komi)
	{
		super(board, komi);
	}

	@Override
	public float calculateScore()
	{
		Field[][] board = super.getBoardCopy();
		// TODO
		return 0;
	}

	@Override
	public Field[][] getPossibleTerritory()
	{
		Field[][] board = super.getBoardCopy();
		// TODO
		return null;
	}
}
