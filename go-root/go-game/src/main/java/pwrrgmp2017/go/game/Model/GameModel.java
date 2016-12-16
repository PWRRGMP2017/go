package pwrrgmp2017.go.game.Model;

import pwrrgmp2017.go.game.GameStates.BeginningState;
import pwrrgmp2017.go.game.GameStates.GameState;
import pwrrgmp2017.go.game.GameStates.GameStateEnum;

public abstract class GameModel
{
	private GameBoard board;
	private boolean[][] possibleMovementsWhite;
	private boolean[][] possibleMovementsBlack;
	private boolean WhiteTurn;
	private GameState state;
	float komi;

	GameModel(GameBoard board, float komi)
	{
		this.board=board;
		this.komi=komi;
		state=new BeginningState();
	}
	
	public boolean[][] getPossibleMovements(String colour)
	{
		if (colour.equals("Black"))
			return possibleMovementsBlack.clone();
		else if (colour.equals("White"))
			return possibleMovementsWhite.clone();
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

	public abstract String calculateTerritory();

	public abstract boolean isTurnPossible(int x, int y);

	public GameStateEnum getState()
	{
		return state.getState();
	}
}
