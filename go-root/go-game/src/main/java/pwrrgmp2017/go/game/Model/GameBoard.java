package pwrrgmp2017.go.game.Model;

public class GameBoard
{
	private Field[][] board;
	private int size;

	enum Field
	{
		WHITESTONE, BLACKSTONE, EMPTY;
	}

	GameBoard(int size)
	{
		this.size = size;
		this.board = new Field[size][size];
	}

	int getSize()
	{
		return size;
	}

	boolean[][] getPossibleMovements(Field concur)
	{
		boolean[][] possibleMovements = new boolean[size][size];
		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				if (board[i][j] == Field.EMPTY)
					possibleMovements[i][j] = true;
				else
					possibleMovements[i][j] = false;
				// zabranie możliwości położenia na pole pomiędzy kamieniami
				// przeciwnika
			}
		}
		return possibleMovements;

	}
}
