package pwrrgmp2017.go.game;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exception.GameStillInProgressException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.game.factory.GameFactory;
import pwrrgmp2017.go.game.factory.GameInfo;
import pwrrgmp2017.go.game.factory.GameInfo.RulesType;

public class JapanGameControllerTest
{
	static GameFactory factory;
	GameController controller;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		factory= GameFactory.getInstance();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
		GameInfo gameInfo = new GameInfo(19, 6.5f, RulesType.JAPANESE, false);
		controller=factory.createGame(gameInfo.getAsString());
	}

	@After
	public void tearDown() throws Exception
	{
	}
	
	@Test
	public void testIsSimpleSituationCalculatingWell()
	{
		try
		{
			controller.initialiseGame(Field.BLACKSTONE);
			controller.addMovement(1, 1, Field.BLACKSTONE);
			controller.addMovement(1, 2, Field.WHITESTONE);
			controller.addMovement(2, 1, Field.BLACKSTONE);
			controller.addMovement(2, 2, Field.WHITESTONE);
			controller.addMovement(3, 1, Field.BLACKSTONE);
			controller.addMovement(3, 2, Field.WHITESTONE);
			controller.addMovement(4, 1, Field.BLACKSTONE);
			controller.addMovement(4, 2, Field.WHITESTONE);
		}
		catch (BadFieldException | GameBegginsException | GameIsEndedException | GameStillInProgressException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		if(controller.calculateScore()>0)
			fail();
	}
	
	@Test
	public void testIsComplexSituationCalculatingWell()
	{
		try
		{
			controller.initialiseGame(Field.BLACKSTONE);
			controller.addMovement(1, 1, Field.BLACKSTONE);
			controller.addMovement(1, 2, Field.WHITESTONE);
			controller.addMovement(2, 1, Field.BLACKSTONE);
			controller.addMovement(2, 2, Field.WHITESTONE);
			controller.addMovement(3, 1, Field.BLACKSTONE);
			controller.addMovement(3, 2, Field.WHITESTONE);
			controller.addMovement(4, 1, Field.BLACKSTONE);
			controller.addMovement(4, 2, Field.WHITESTONE);
			controller.addMovement(1, 3, Field.BLACKSTONE);
			controller.addMovement(1, 4, Field.WHITESTONE);
			controller.addMovement(2, 3, Field.BLACKSTONE);
			controller.addMovement(2, 4, Field.WHITESTONE);
			controller.addMovement(3, 3, Field.BLACKSTONE);
			controller.addMovement(3, 4, Field.WHITESTONE);
			controller.addMovement(4, 3, Field.BLACKSTONE);
			controller.addMovement(4, 4, Field.WHITESTONE);
			controller.addMovement(12, 12, Field.BLACKSTONE);
			controller.addMovement(13, 13, Field.WHITESTONE);
			controller.addMovement(3, 7, Field.BLACKSTONE);
			controller.addMovement(4, 7, Field.WHITESTONE);
			controller.addMovement(11, 11, Field.BLACKSTONE);
			controller.addMovement(11, 9, Field.WHITESTONE);
			controller.addMovement(11, 10, Field.BLACKSTONE);
		}
		catch (BadFieldException | GameBegginsException | GameIsEndedException | GameStillInProgressException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		if(controller.calculateScore()>0)
			fail();
	}
	
	@Test
	public void testIsDivisionIntoTwoAreasSituationCalculatingWell()
	{
		try
		{
			controller.initialiseGame(Field.BLACKSTONE);
			controller.addMovement(10, 1, Field.BLACKSTONE);
			controller.addMovement(11, 1, Field.WHITESTONE);
			controller.addMovement(10, 2, Field.BLACKSTONE);
			controller.addMovement(11, 2, Field.WHITESTONE);
			controller.addMovement(10, 3, Field.BLACKSTONE);
			controller.addMovement(11, 3, Field.WHITESTONE);
			controller.addMovement(10, 4, Field.BLACKSTONE);
			controller.addMovement(11, 4, Field.WHITESTONE);
			controller.addMovement(10, 5, Field.BLACKSTONE);
			controller.addMovement(11, 5, Field.WHITESTONE);
			controller.addMovement(10, 6, Field.BLACKSTONE);
			controller.addMovement(11, 6, Field.WHITESTONE);
			controller.addMovement(10, 7, Field.BLACKSTONE);
			controller.addMovement(11, 7, Field.WHITESTONE);
			controller.addMovement(10, 8, Field.BLACKSTONE);
			controller.addMovement(11, 8, Field.WHITESTONE);
			controller.addMovement(10, 9, Field.BLACKSTONE);
			controller.addMovement(11, 9, Field.WHITESTONE);
			controller.addMovement(10, 10, Field.BLACKSTONE);
			controller.addMovement(11, 10, Field.WHITESTONE);
			controller.addMovement(10, 11, Field.BLACKSTONE);
			controller.addMovement(11, 11, Field.WHITESTONE);
			controller.addMovement(10, 12, Field.BLACKSTONE);
			controller.addMovement(11, 12, Field.WHITESTONE);
			controller.addMovement(10, 13, Field.BLACKSTONE);
			controller.addMovement(11, 13, Field.WHITESTONE);
			controller.addMovement(10, 14, Field.BLACKSTONE);
			controller.addMovement(11, 14, Field.WHITESTONE);
			controller.addMovement(10, 15, Field.BLACKSTONE);
			controller.addMovement(11, 15, Field.WHITESTONE);
			controller.addMovement(10, 16, Field.BLACKSTONE);
			controller.addMovement(11, 16, Field.WHITESTONE);
			controller.addMovement(10, 17, Field.BLACKSTONE);
			controller.addMovement(11, 17, Field.WHITESTONE);
			controller.addMovement(10, 18, Field.BLACKSTONE);
			controller.addMovement(11, 18, Field.WHITESTONE);
			controller.addMovement(10, 19, Field.BLACKSTONE);
			controller.addMovement(11, 19, Field.WHITESTONE);
			
		}
		catch (BadFieldException | GameBegginsException | GameIsEndedException | GameStillInProgressException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		if(controller.calculateScore()!=12.5)
			fail(((Float)controller.calculateScore()).toString());
		Field[][] boardCopy= controller.getBoardCopy();
		for(int i=0; i<boardCopy.length; i++)
		{
			System.out.println();
			for(int j=0; j<boardCopy.length; j++)
			{
				switch(boardCopy[i][j])
				{
				case WALL:
					System.out.print('X');
					break;
				case BLACKSTONE:
					System.out.print('B');
					break;
				case WHITESTONE:
					System.out.print('W');
					break;
				case BLACKTERRITORY:
					System.out.print('b');
					break;
				case WHITETERRITORY:
					System.out.print('w');
					break;
				case EMPTY:
				case NONETERRITORY:
					System.out.print('-');
					break;
				default:
				}
			}
		}
		try
		{
			controller.addMovement(7, 8, Field.BLACKSTONE);
			controller.addMovement(7, 6, Field.WHITESTONE);
		}
		catch (BadFieldException | GameBegginsException | GameIsEndedException e)
		{
			e.printStackTrace();
		}
		Field[][] terytoria= controller.getPossibleTerritory();
		if(controller.calculateScore(terytoria)>0)
			fail("Zły wynik przy 'inwazji':"+controller.calculateScore(terytoria)); 
	}

	@Test
	public void testIsCalculatingWellWithEyes()
	{
		try
		{
			controller.initialiseGame(Field.BLACKSTONE);
			controller.addMovement(10, 10, Field.BLACKSTONE);
			controller.addMovement(9, 10, Field.WHITESTONE);
			controller.addMovement(10, 11, Field.BLACKSTONE);
			controller.addMovement(9, 11, Field.WHITESTONE);
			controller.addMovement(10, 12, Field.BLACKSTONE);
			controller.addMovement(9, 12, Field.WHITESTONE);
			controller.addMovement(10, 13, Field.BLACKSTONE);
			controller.addMovement(9, 13, Field.WHITESTONE);
			controller.addMovement(10, 14, Field.BLACKSTONE);
			controller.addMovement(9, 14, Field.WHITESTONE);
			controller.addMovement(10, 15, Field.BLACKSTONE);
			controller.addMovement(9, 15, Field.WHITESTONE);
			controller.addMovement(12, 10, Field.BLACKSTONE);
			controller.addMovement(13, 10, Field.WHITESTONE);
			controller.addMovement(12, 11, Field.BLACKSTONE);
			controller.addMovement(13, 11, Field.WHITESTONE);
			controller.addMovement(12, 12, Field.BLACKSTONE);
			controller.addMovement(13, 12, Field.WHITESTONE);
			controller.addMovement(12, 13, Field.BLACKSTONE);
			controller.addMovement(13, 13, Field.WHITESTONE);
			controller.addMovement(12, 14, Field.BLACKSTONE);
			controller.addMovement(13, 14, Field.WHITESTONE);
			controller.addMovement(12, 15, Field.BLACKSTONE);
			controller.addMovement(13, 15, Field.WHITESTONE);
			
			controller.addMovement(11, 15, Field.BLACKSTONE);
			controller.addMovement(10, 9, Field.WHITESTONE);
			controller.addMovement(11, 12, Field.BLACKSTONE);
			controller.addMovement(11, 9, Field.WHITESTONE);
			controller.addMovement(11, 10, Field.BLACKSTONE);
			controller.addMovement(12, 9, Field.WHITESTONE);
			controller.addMovement(11, 17, Field.BLACKSTONE);
			controller.addMovement(9, 16, Field.WHITESTONE);
			controller.addMovement(12, 17, Field.BLACKSTONE);
			controller.addMovement(9, 17, Field.WHITESTONE);
			controller.addMovement(10, 17, Field.BLACKSTONE);
			controller.addMovement(14, 16, Field.WHITESTONE);
			controller.addMovement(10, 16, Field.BLACKSTONE);
			controller.addMovement(14, 17, Field.WHITESTONE);
			controller.addMovement(13, 17, Field.BLACKSTONE);
			controller.addMovement(10, 18, Field.WHITESTONE);
			controller.addMovement(13, 16, Field.BLACKSTONE);
			controller.addMovement(11, 18, Field.WHITESTONE);
			controller.pass(Field.BLACKSTONE);
			controller.addMovement(12, 18, Field.WHITESTONE);
			controller.pass(Field.BLACKSTONE);
			controller.addMovement(13, 18, Field.WHITESTONE);
			
		}
		catch (BadFieldException | GameBegginsException | GameIsEndedException | GameStillInProgressException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		if(controller.calculateScore()>20)
			fail(((Float)controller.calculateScore()).toString());
	}
}
