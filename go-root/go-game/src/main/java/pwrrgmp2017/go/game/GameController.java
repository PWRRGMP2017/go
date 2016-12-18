package pwrrgmp2017.go.game;

import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.GameStates.GameStateEnum;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.game.Model.GameModel;

public class GameController
{
	private GameModel model;

	public GameController(GameModel model)
	{
		this.model = model;
	}

	public boolean[][] getPossibleMovements(Field colour) throws BadFieldException
	{
		return model.getPossibleMovements(colour);
	}

	boolean addMovement(int x, int y, Field playerField)
	{
		return model.addMovement(x, y, playerField);
	}

	boolean isTurnPossible(int x, int y, Field playerField) throws BadFieldException
	{
		return model.isTurnPossible(x, y, playerField);
	}
	
	public float calculateScore()
	{
		return model.calculateScore();
	}
	
	public Field[][] getPossibleTerritory()
	{
		return model.getPossibleTerritory();
	}
	
	public GameStateEnum getState()
	{
		return model.getState();
	}
	
	public Field[][] getBoardCopy()
	{
		return model.getBoardCopy();
	}
	
	public float getKomi()
	{
		return model.getKomi();
	}
}
