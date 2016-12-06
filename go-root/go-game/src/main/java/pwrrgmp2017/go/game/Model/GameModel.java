package pwrrgmp2017.go.game.Model;

public abstract class GameModel
{
	private GameBoard board;
	private boolean[][] possibleMovements;

	public boolean[][] getPossibleMovements()
	{
		return possibleMovements;
	}

	public abstract String addMovement(String move);

	public abstract String calculate();
}
