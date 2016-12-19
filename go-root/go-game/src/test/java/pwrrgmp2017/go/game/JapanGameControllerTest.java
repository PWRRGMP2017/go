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
import pwrrgmp2017.go.game.Model.GameBoard;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.game.Model.GameModel;
import pwrrgmp2017.go.game.Model.JapanGameModel;
import pwrrgmp2017.go.game.factory.GameFactory;

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
		//String gameInfo="JAPAN_019_M_06.5";
		GameBoard board= new GameBoard(19);
		GameModel model= new JapanGameModel(board, (float) 6.5);
		controller= new GameController(model);
		//controller=factory.createGame(gameInfo);
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testSimplePointsCalculate()
	{
		try
		{
			controller.initialiseGame();
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
	public void testComplexPointsCalculate()
	{
		try
		{
			controller.initialiseGame();
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
	public void testDivisionIntoTwoAreasSituationPointsCalculate()
	{
		try
		{
			controller.initialiseGame();
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
		if(controller.calculateScore()>0)
			fail();
	}

}
