package pwrrgmp2017.go.game;

import java.awt.Point;

import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.GameStates.GameStateEnum;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.game.Model.GameModel;

public class GameController
{
	private GameModel model;
	protected int lastMoveX;
	protected int lastMoveY;

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
		if(!model.addMovement(x, y, playerField))
			return false;
		
		lastMoveX=x;
		lastMoveY=y;
		return true;
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
	
	public void initialiseGame()
	{
		model.initialiseGame();
	}
	
	public void pass()
	{
		model.pass();
	}
	
	public void resign()
	{
		model.resign();
	}
	
	public Point getLastMovement()
	{
		return new Point( lastMoveX, lastMoveY);
	}
}
