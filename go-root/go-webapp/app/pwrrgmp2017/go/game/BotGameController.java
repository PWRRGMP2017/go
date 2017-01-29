package pwrrgmp2017.go.game;

import java.util.Random;

import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.GameStates.GameStateEnum;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.game.Model.GameModel;

/**
 * Class which represents Controller with bot 
 * @author Robert Gawlik
 *
 */
public class BotGameController extends GameController
{
	Random rand;
	/**Current model of the game */
	GameModel model;
	/**Colour of bot Player */
	Field botColour;
	
	/**
	 * Construcotor of the class
	 * @param model Model of the game
	 */
	public BotGameController(GameModel model)
	{
		super(model);
		this.model=model;
		rand = new Random();
	}
	
	/**
	 * Adds player's and next bot movements on the board 
	 * @param x first attribute position
	 * @param y second attribute position
	 * @param playerField
	 * @throws BadFieldException
	 * @throws GameBegginsException
	 * @throws GameIsEndedException
	 */
	@Override
	public void addMovement(int x, int y, Field playerField) throws BadFieldException, GameBegginsException, GameIsEndedException
	{
		model.addMovement(x, y, playerField); 

		if(playerField==Field.BLACKSTONE)
			botColour=Field.WHITESTONE;
		else
			botColour=Field.BLACKSTONE;
		
		boolean[][] moves =null;

		moves=model.getPossibleMovements(botColour);

		int movesCounter=0;
		for(int i=0; i<moves.length; i++)
			for(int j=0; j<moves.length; j++)
			{
				if(moves[i][j]==true)
					movesCounter++;
			}
		if(movesCounter==0)
		{
			super.lastMoveX=x;
			super.lastMoveY=y;
			super.pass(botColour);
		}
		else
		{
			if(movesCounter!=1)
				movesCounter=rand.nextInt(movesCounter-1);
			else
				movesCounter=0;
			
			for(int i=0; i<moves.length; i++)
				for(int j=0; j<moves.length; j++)
				{
					if(moves[i][j]==true)
					{
						if(movesCounter==0)
						{
							model.addMovement(i+1, j+1, botColour);
							super.lastMoveX=i+1;
							super.lastMoveY=j+1;
							return;
						}
						else
							movesCounter--;
					}
				}
		}
	}
	
	/**
	 * Passing by the player 
	 * (the same what resign() when second player is bot)
	 * @param colour Colour of player
	 * @throws GameBegginsException Bad state
	 * @throws GameIsEndedException Bad state
	 * @throws BadFieldException Field isn't represented stone
	 */
	@Override
	public void pass(Field colour) throws GameBegginsException, GameIsEndedException, BadFieldException
	{
		super.pass(colour);
		if(model.getState()!=GameStateEnum.END)
			super.pass(Field.WHITESTONE);
	}
	
}
