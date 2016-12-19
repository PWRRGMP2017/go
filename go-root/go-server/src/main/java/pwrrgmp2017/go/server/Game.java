package pwrrgmp2017.go.server;

import java.awt.Point;

import pwrrgmp2017.go.game.GameController;
import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.server.Exceptions.BadPlayerException;
import pwrrgmp2017.go.server.connection.PlayerConnection;

public class Game extends Thread
{
	private GameController controller;
	private PlayerConnection blackPlayer;
	private PlayerConnection whitePlayer;

	Game(PlayerConnection blackPlayer, PlayerConnection whitePlayer, GameController controller)
	{
		this.controller = controller;
		this.blackPlayer = blackPlayer;
		this.whitePlayer = whitePlayer;
	}

	public GameController getController()
	{
		return controller;
	}

	public PlayerConnection getBlackPlayer()
	{
		return blackPlayer;
	}

	public PlayerConnection getWhitePlayer()
	{
		return whitePlayer;
	}

	public PlayerConnection getOpponent(PlayerConnection player)
	{
		return player == blackPlayer ? whitePlayer : blackPlayer;
	}
	
	public void addTurnMessage(PlayerConnection player, int x, int y) throws BadPlayerException, BadFieldException, GameBegginsException, GameIsEndedException 
	{ 
		if(player==blackPlayer) 
			controller.addMovement(x, y, Field.BLACKSTONE); 
		else if(player==whitePlayer)
			controller.addMovement(x, y, Field.WHITESTONE);
		
	} 
	
	public void resign() throws GameIsEndedException
	{
		controller.resign();
	}
	
	public void pass(PlayerConnection player) throws GameBegginsException, GameIsEndedException, BadFieldException
	{
		if(player==blackPlayer) 
			controller.pass(Field.BLACKSTONE);
		else if(player==whitePlayer)
			controller.pass(Field.WHITESTONE);
	}
	
	public float calculateScore()
	{
		return controller.calculateScore();
	}
	
	public void calculateScore(Field[][] territory)
	{
		controller.calculateScore(territory);
	}
	
	public Field[][] getBoardCopy()
	{
		return controller.getBoardCopy();
	}
	
	public Point getLastMovement()
	{
		return controller.getLastMovement();
	}
	
	public boolean[][] getPossibleMovements(Field colour) throws BadFieldException
	{
		return controller.getPossibleMovements(colour);
	}
	
	public Field[][] getPossibleTerritory()
	{
		return controller.getPossibleTerritory();
	}
	
}
