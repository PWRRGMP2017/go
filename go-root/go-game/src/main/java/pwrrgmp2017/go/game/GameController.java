package pwrrgmp2017.go.game;

import pwrrgmp2017.go.game.Model.GameModel;

public class GameController
{
	private GameModel model;

	public GameController(GameModel model)
	{
		this.model = model;
	}

	public boolean[][] getPossibleMovements(String colour)
	{
		return model.getPossibleMovements(colour);
	}

	String addMovement(String move)
	{
		return model.addMovement(move);
	}

	boolean isTurnPossible(int x, int y)
	{
		return model.isTurnPossible(x, y);
	}
}
