package pwrrgmp2017.go.server.Model;

public class GameModel
{
	private GameBoard board;
	private boolean[][] possibleMovements;

	GameModel()
	{
		board= new GameBoard();
	}

	public boolean[][] getPossibleMovements()
	{
		return possibleMovements;
	}

	public String addMovement(String move)
	{
		//TODO 
		return move;
	}
}
