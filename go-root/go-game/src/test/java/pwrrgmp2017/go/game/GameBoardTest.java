package pwrrgmp2017.go.game;

import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.Model.GameBoard;
import pwrrgmp2017.go.game.Model.GameBoard.Field;

public class GameBoardTest
{
	Class<?> classBoard;
	Constructor<?> constructor;
	Method method;
	GameBoard board;
	
	@Before
	public void setUp() throws Exception
	{
		classBoard=GameBoardTest.class;
		try
		{
			this.constructor=classBoard.getDeclaredConstructor(int.class);
			board= (GameBoard) constructor.newInstance(19);
		}
		catch(NoSuchMethodException e)
		{
			System.out.println(e.getMessage());
		}
		catch(SecurityException e)
		{
			System.out.println(e.getMessage());
		}
		catch(IllegalAccessException e)
		{
			System.out.println(e.getMessage());
		}
		catch(IllegalArgumentException e)
		{
			System.out.println(e.getMessage());
		}
		catch(InstantiationException e)
		{
			System.out.println(e.getMessage());
		}
		catch(InvocationTargetException e)
		{
			System.out.println(e.getMessage());
		}
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void IsBoardInitialisedClean()
	{
		if(board==null)
			fail();
		try
		{
			method=board.getClass().getMethod("getPossibleMovements");
		}
		catch (NoSuchMethodException e)
		{
			fail(e.getMessage());
		}
		catch (SecurityException e)
		{
			fail(e.getMessage());
		}
		method.setAccessible(true);
		boolean[][] moves =null;
		try
		{
			moves=board.getPossibleMovements(Field.BLACKSTONE);
		}
		catch (BadFieldException e)
		{
			fail(e.getMessage());
		}
		for(int i=0; i<19; i++)
			for(int j=0; j<19; j++)
				Assert.assertEquals(true, moves[i][j]);
	}

}
