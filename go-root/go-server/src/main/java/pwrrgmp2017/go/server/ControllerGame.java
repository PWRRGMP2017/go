package pwrrgmp2017.go.server;

import pwrrgmp2017.go.server.Model.GameModel;

public class ControllerGame
{
	private GameModel model;
	private Game game;

	ControllerGame(GameModel model, Game game)
	{
		this.model= model;
		this.game= game;
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
