package pwrrgmp2017.go.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.Model.GameBoard;
import pwrrgmp2017.go.game.Model.GameBoard.Field;

public class GameBoardTest
{
	Class<?> classBoard;
	Constructor<?> constructor;
	Method method;
	Object board;
	GameBoard boardReal;
	boolean[][] moves =null;
	java.lang.reflect.Field[] fields;
	
	@Before
	public void setUp() throws Exception
	{
		classBoard=GameBoard.class;
		try
		{
			this.constructor=classBoard.getDeclaredConstructor(int.class);
			constructor.setAccessible(true);
			fields=classBoard.getDeclaredFields();
			for(java.lang.reflect.Field f: fields)
			{
				f.setAccessible(true);
			}
			board= constructor.newInstance(19);
			boardReal=(GameBoard) board;
		}
		catch(NoSuchMethodException e)
		{
			System.out.println("no such method");
		}
		catch(IllegalAccessException e)
		{
			System.out.println("illegal acces");
		}
		catch(IllegalArgumentException e)
		{
			System.out.println("illegal argument");
		}
		catch(InstantiationException e)
		{
			System.out.println("instantion");
		}
		catch(InvocationTargetException e)
		{
			System.out.println("invocation target");
		}
	}

	@After
	public void tearDown() throws Exception
	{
	}
	
	@Test
	public void areTestsWellSetUp()
	{
		if(classBoard==null)
			fail();
		if(board==null)
			fail();
	}
	
	@Test
	public void IsBoardInitialisedClean()
	{
		
		try
		{
			moves=boardReal.getPossibleMovements(Field.BLACKSTONE);
		}
		catch (BadFieldException e)
		{
			fail(e.getMessage());
		}
		for(int i=0; i<19; i++)
			for(int j=0; j<19; j++)
				assertEquals(true, moves[i][j]);
		assertSame(moves.length, 19);
	}
	
	@Test
	public void isGetPossibleMovementsReturningWellInSimpleSituation()
	{

		try
		{
			java.lang.reflect.Field field=boardReal.getClass().getDeclaredField("board");
			field.setAccessible(true);
			Field[][] boardArray;
			boardArray = (Field[][]) field.get(boardReal);
			boardArray[5][6]=Field.BLACKSTONE;
			boardArray[4][2]=Field.BLACKSTONE;
			boardArray[3][3]=Field.WHITESTONE;
			boardArray[10][12]=Field.WHITESTONE;
			boardArray[15][5]=Field.WHITESTONE;
			field.set(boardReal, boardArray);
			moves=boardReal.getPossibleMovements(Field.BLACKSTONE);
			Assert.assertEquals(false, moves[4][5]);
			Assert.assertEquals(false, moves[3][1]);
			Assert.assertEquals(false, moves[2][2]);
			Assert.assertEquals(false, moves[9][11]);
			Assert.assertEquals(false, moves[14][4]);
			Assert.assertEquals(true, moves[1][1]);
			Assert.assertEquals(true, moves[15][15]);
			Assert.assertEquals(true, moves[13][13]);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (BadFieldException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	public void isGetPossibleMovementsReturningWellInSimpleNoBriefs()
	{

		try
		{
			java.lang.reflect.Field field=boardReal.getClass().getDeclaredField("board");
			field.setAccessible(true);
			Field[][] boardArray;
			boardArray = (Field[][]) field.get(boardReal);
			boardArray[5][6]=Field.BLACKSTONE;
			boardArray[5][4]=Field.BLACKSTONE;
			boardArray[6][5]=Field.BLACKSTONE;
			boardArray[4][5]=Field.BLACKSTONE;
			boardArray[15][5]=Field.WHITESTONE;
			field.set(boardReal, boardArray);
			moves=boardReal.getPossibleMovements(Field.WHITESTONE);
			Assert.assertEquals(false, moves[4][4]); // niemożliwe położenie kamienia w miejscu bez oddechów
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (BadFieldException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
			fail();
		}
		
	}
}
