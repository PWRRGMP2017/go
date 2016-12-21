package pwrrgmp2017.go.game;

import java.util.Random;

import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
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
	public void addMovement(int x, int y, Field playerField) throws BadFieldException, GameBegginsException, GameIsEndedException
	{
		super.addMovement(x, y, playerField);

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
			super.pass(Field.WHITESTONE);
		else
		{
			movesCounter=rand.nextInt(movesCounter-1);
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
						}
						else
							movesCounter--;
					}
				}
		}
	}
	
	@Override
	public void pass(Field colour) throws GameBegginsException, GameIsEndedException, BadFieldException
	{
		super.pass(colour);
		if(model.getState()!=GameStateEnum.END)
			super.pass(Field.WHITESTONE);
	}
	
}
