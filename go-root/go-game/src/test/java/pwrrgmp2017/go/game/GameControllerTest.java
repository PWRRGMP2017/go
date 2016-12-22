package pwrrgmp2017.go.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.awt.Point;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exception.GameStillInProgressException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.GameStates.GameStateEnum;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.game.factory.GameFactory;
import pwrrgmp2017.go.game.factory.GameInfo;
import pwrrgmp2017.go.game.factory.GameInfo.RulesType;

public class GameControllerTest
{
	GameController controller, botController;
	GameFactory factory;
	GameInfo info;
	
	@Before
	public void setUp() throws Exception
	{
		info= new GameInfo(10, (float)6.5, RulesType.JAPANESE, false);
		factory=GameFactory.getInstance();
		controller=factory.createGame(info.getAsString());
		info= new GameInfo(10, (float)6.5, RulesType.JAPANESE, true);
		botController=factory.createGame(info.getAsString());
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void isControllerWorkingWellAtBeginningTest()
	{
		try
		{
			controller.addMovement(1, 1, Field.BLACKSTONE);
			fail("Dodawanie ruchów od razu");
		}
		catch (BadFieldException | GameBegginsException | GameIsEndedException e)
		{
			try
			{
				controller.initialiseGame(Field.BLACKSTONE);
				
				if(controller.calculateScore()!=-6.5)
					fail("Złe obliczanie wyniku");
				
				if(controller.lastMoveX+controller.lastMoveY!=0)
					fail("Nie wyzerowane wartości ostatniego ruchu");
				
				if(controller.getBlackCaptives()+controller.getWhiteCaptives()!=0)
					fail("Jeńcy na początku gry");
				
				if(controller.getKomi()!=6.5)
					fail("Źle określone komi");
				
				if(controller.isTurnPossible(1, 1, Field.BLACKSTONE)==false)
					fail("Brak możliwości wykonania prostego ruchu");
			}
			catch (GameStillInProgressException | BadFieldException e1)
			{
				fail(e1.getMessage());
			}

		}
	}
		
		@Test
		public void isGameModelChangingStatesWellTest()
		{
			try
			{
				assertSame("Zły state na początku gry", GameStateEnum.BEGINNING, controller.getState());
				
				controller.initialiseGame(Field.BLACKSTONE);
				assertSame("Brak inicjalizacji gry lub zły kolor początkowego kamienia", GameStateEnum.BLACKMOVE, controller.getState());
				
				controller.addMovement(1, 1, Field.BLACKSTONE);
				assertSame("Nie zmienia kolei gracza po dodaniu ruchu", GameStateEnum.WHITEMOVE, controller.getState());
				
				
				controller.pass(Field.WHITESTONE);
				assertSame("Nie zmienia kolei gracza po pass'owaniu", GameStateEnum.BLACKMOVE, controller.getState());
				
				controller.pass(Field.BLACKSTONE);
				assertSame("Nie zmienia na END przy drugim pass'owaniu", GameStateEnum.END, controller.getState());
				
				controller.initialiseGame(Field.WHITESTONE);
				assertSame("Nie przywraca gry do konkretnego Playera", GameStateEnum.WHITEMOVE, controller.getState());
				
			}
			catch (GameStillInProgressException | BadFieldException | GameBegginsException | GameIsEndedException e)
			{
				fail(e.getMessage());
			}
			
		}
		
		@Test
		public void isGameControllerThrowingRightExceptionsTest()
		{
			try
			{
				try
				{
					controller.addMovement(1, 2, Field.BLACKSTONE);
				}
				catch (BadFieldException | GameIsEndedException e)
				{
					fail(e.getMessage());
				}
				fail("Nie wyrzuca wyjątku");
			}
			catch (GameBegginsException e)
			{}
			
			try
			{
				controller.initialiseGame(Field.BLACKSTONE);
			}
			catch (GameStillInProgressException | BadFieldException e)
			{
				fail(e.getMessage());
			}
			try
			{
				controller.addMovement(1, 2, Field.WHITESTONE);
				fail("Nie wyrzuca wyjątku BadFieldException");
			}
			catch (GameBegginsException | GameIsEndedException e)
			{
				fail("Zły wyjątek"+e.getMessage());
			}
			catch (BadFieldException e)
			{}
			
			try
			{
				controller.addMovement(2, 2, Field.BLACKSTONE);
			}
			catch (BadFieldException | GameBegginsException | GameIsEndedException e)
			{
				fail("Zły wyjątek"+e.getMessage());
			}
			
			try
			{
				controller.addMovement(2, 2, Field.BLACKSTONE);
				fail("Brak wyjątku BadField");
			}
			catch (GameBegginsException | GameIsEndedException e)
			{
				fail("Zły wyjątek"+e.getMessage());
			}
			catch (BadFieldException e)
			{}
			
			try
			{
				controller.addMovement(2, 2, Field.WHITESTONE);
				fail("Brak wyjątku BadField");
			}
			catch (GameBegginsException | GameIsEndedException e)
			{
				fail("Zły wyjątek");
			}
			catch (BadFieldException e)
			{}
			
			try
			{
				controller.pass(Field.WHITESTONE);
			}
			catch (GameBegginsException | GameIsEndedException | BadFieldException e)
			{
				fail("Zły wyjątek"+e.getMessage());
			}
			
			try
			{
				controller.pass(Field.BLACKSTONE);
			}
			catch (GameBegginsException | GameIsEndedException | BadFieldException e)
			{
				fail("Zły wyjątek"+e.getMessage());
			}
			
			Point p=controller.getLastMovement();
			assertEquals(2, (int)p.getX());
			assertEquals(2, (int)p.getY());
			
			assertEquals(GameStateEnum.END, controller.getState());
			
			try
			{
				controller.addMovement(3, 4, Field.BLACKSTONE);
				fail("Brak wyjątku GameIsEnded");
			}
			catch (BadFieldException | GameBegginsException  e)
			{
				fail("Zły wyjątek"+e.getMessage());
			}
			catch (GameIsEndedException e)
			{}
			
			try
			{
				controller.initialiseGame(Field.WHITESTONE);
				controller.resign();
			}
			catch (GameIsEndedException | GameStillInProgressException | BadFieldException e)
			{
				fail("Zły wyjątek"+ e.getMessage());
			}
			
		}
		
		@Test
		public void isBotWorkingWellTest()
		{
			try
			{
				botController.initialiseGame(Field.BLACKSTONE);
				botController.addMovement(1, 2, Field.BLACKSTONE);
				assertSame(GameStateEnum.BLACKMOVE, botController.getState());
				boolean[][] moves=botController.getPossibleMovements(Field.BLACKSTONE);
				int movesCount=0;
				for(int i=0; i<moves.length; i++)
					for(int j=0; j<moves.length; j++)
						if(moves[i][j]==true)
						{
							movesCount++;
						}
				assertEquals(moves.length*moves.length-2, movesCount);
				assertNotSame(102, botController.lastMoveX*100+botController.lastMoveY);
				
				if(botController.isTurnPossible(5, 5, Field.WHITESTONE))
					botController.addMovement(5, 5, Field.BLACKSTONE);
				
				botController.pass(Field.BLACKSTONE);
				
				assertEquals(GameStateEnum.END, botController.getState());
				
			}
			catch (GameStillInProgressException | BadFieldException | GameBegginsException | GameIsEndedException e)
			{
				e.printStackTrace();
				fail();
			}
		}

}
