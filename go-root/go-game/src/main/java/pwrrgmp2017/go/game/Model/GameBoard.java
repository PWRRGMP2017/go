package pwrrgmp2017.go.game.Model;

public class GameBoard
{
	Field[][] board;
	int size;

	enum Field
	{
		WHITESTONE, BLACKSTONE, EMPTY;
	}

	GameBoard(int size)
	{
		this.size = size;
		this.board = new Field[size][size];
	}
}
