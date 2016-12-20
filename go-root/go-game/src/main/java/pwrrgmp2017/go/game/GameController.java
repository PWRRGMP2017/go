package pwrrgmp2017.go.game;

import java.awt.Point;

import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exception.GameStillInProgressException;
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

	public void addMovement(int x, int y, Field playerField) throws BadFieldException, GameBegginsException, GameIsEndedException
	{
		model.addMovement(x, y, playerField);
		
		lastMoveX=x;
		lastMoveY=y;
	}

	boolean isTurnPossible(int x, int y, Field playerField) throws BadFieldException
	{
		return model.isTurnPossible(x, y, playerField);
	}
	
	public float calculateScore()
	{
		return model.calculateScore();
	}
	
	public float calculateScore(Field[][] territory)
	{
		return model.calculateScore(territory);
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
	
	public void initialiseGame() throws GameStillInProgressException
	{
		model.initialiseGame();
	}
	
	public void pass(Field colour) throws GameBegginsException, GameIsEndedException, BadFieldException
	{
		model.pass(colour);
	}
	
	public void resign() throws GameIsEndedException
	{
		model.resign();
	}
	
	public Point getLastMovement()
	{
		return new Point( lastMoveX, lastMoveY);
	}
	
	public int getBlackCaptives()
	{
		return model.getBlackCaptives();
	}
	
	public int getWhiteCaptives()
	{
		return model.getWhiteCaptives();
	}
}
