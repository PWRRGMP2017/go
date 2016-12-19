package pwrrgmp2017.go.game;

import java.util.Random;

import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.GameStates.GameStateEnum;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.game.Model.GameModel;

public class BotGameController extends GameController
{
	Random rand;
	GameModel model;
	Field botColour;
	public BotGameController(GameModel model)
	{
		super(model);
		this.model=model;
	}
	
	@Override
	boolean addMovement(int x, int y, Field playerField)
	{
		if(!super.addMovement(x, y, playerField))
			return false;
		if(playerField==Field.BLACKSTONE)
			botColour=Field.WHITESTONE;
		else
			botColour=Field.BLACKSTONE;
		
		boolean[][] moves =null;
		try
		{
			moves=model.getPossibleMovements(botColour);
		}
		catch (BadFieldException e)
		{
		}
		int movesCounter=0;
		for(int i=0; i<moves.length; i++)
			for(int j=0; j<moves.length; j++)
			{
				if(moves[i][j]==true)
					movesCounter++;
			}
		if(movesCounter==0)
			super.pass();
		else
		{
			movesCounter=rand.nextInt(movesCounter);
			for(int i=0; i<moves.length; i++)
				for(int j=0; j<moves.length; j++)
				{
					if(moves[i][j]==true)
					{
						if(movesCounter==0)
						{
							super.addMovement(i+1, j+1, botColour);
						}
						else
							movesCounter--;
						
					}
				}
		}
		return true;
	}
	
	@Override
	public void pass()
	{
		super.pass();
		if(model.getState()!=GameStateEnum.END)
			super.pass();
	}
	
}
