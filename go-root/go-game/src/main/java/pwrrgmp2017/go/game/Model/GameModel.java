package pwrrgmp2017.go.game.Model;

public abstract class GameModel
{
	private GameBoard board;
	private boolean[][] possibleMovementsWhite;
	private boolean[][] possibleMovementsBlack;

	public boolean[][] getPossibleMovements(String colour)
	{
		if (colour.equals("Black"))
			return possibleMovementsBlack;
		else if (colour.equals("White"))
			return possibleMovementsWhite;
		else
		{
			int size = board.getSize();
			boolean[][] possibleMovements = new boolean[size][size];
			for (int i = 0; i < size; i++)
				for (int j = 0; j < size; j++)
				{
					if (possibleMovementsWhite[i][j] == true || possibleMovementsBlack[i][j] == true)
						possibleMovements[i][j] = true;
					else
						possibleMovements[i][j] = false;
				}
			return possibleMovements;
		}
	}

	public abstract String addMovement(String move);

	public abstract String calculate();

	public abstract boolean isTurnPossible(int x, int y);
}
