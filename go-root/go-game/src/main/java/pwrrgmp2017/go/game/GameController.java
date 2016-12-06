package pwrrgmp2017.go.game;

import pwrrgmp2017.go.game.Model.GameModel;

public class GameController
{
	private GameModel model;

	public GameController(GameModel model)
	{
		this.model = model;
	}

	public boolean[][] getPossibleMovements()
	{
		return model.getPossibleMovements();
	}

	String addMovement(String move)
	{
		return model.addMovement(move);
	}
}
