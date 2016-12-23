package pwrrgmp2017.go.game.Model;

import pwrrgmp2017.go.game.Model.GameBoard.Field;

/**
 * Unimplemented class which represents Chinese game
 * @author Robert Gawlik
 *
 */
public class ChineseGameModel extends GameModel
{

	public ChineseGameModel(GameBoard board, float komi)
	{
		super(board, komi);
	}

	/**
	 * Method which calculates the score of current situation
	 */
	@Override
	public float calculateScore()
	{
//		Field[][] board = super.getBoardCopy();
		// TODO
		return 0;
	}

	/**
	 * Method which returns possible territory
	 */
	@Override
	public Field[][] getPossibleTerritory()
	{
//		Field[][] board = super.getBoardCopy();
		// TODO
		return null;
	}

	/**
	 * Method which calculates score of situation in parametre
	 */
	@Override
	public float calculateScore(Field[][] territory)
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
