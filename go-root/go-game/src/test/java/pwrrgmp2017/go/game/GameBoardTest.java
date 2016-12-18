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
	boolean[][] moves;
	java.lang.reflect.Field[] fields;
	Field[][] boardArray;
	java.lang.reflect.Field field;
	
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
			field=boardReal.getClass().getDeclaredField("board");
			field.setAccessible(true);
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
	public void areTestsWellSetUpTest()
	{
		if(classBoard==null)
			fail();
		if(constructor==null)
			fail();
		if(board==null)
			fail();
		if(boardReal==null)
			fail();
		if(fields==null)
			fail();
	}
	
	@Test
	public void IsBoardInitialisedCleanTest()
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
		assertEquals(0, boardReal.getBlackCaptives());
		assertEquals(0, boardReal.getWhiteCaptives());
		assertEquals(19, boardReal.getSize());
	}
	
	@Test
	public void makeMovementSimpleTest()
	{
		if(!boardReal.makeMovement(2, 2, Field.BLACKSTONE, Field.WHITESTONE))
			fail();
		if(!boardReal.makeMovement(3, 2, Field.BLACKSTONE, Field.WHITESTONE))
			fail();
		if(!boardReal.makeMovement(7, 7, Field.WHITESTONE, Field.BLACKSTONE))
			fail();
		if(!boardReal.makeMovement(7, 5, Field.WHITESTONE, Field.BLACKSTONE))
			fail();
		if(!boardReal.makeMovement(3, 14, Field.BLACKSTONE, Field.WHITESTONE))
			fail();
		try
		{
			moves=boardReal.getPossibleMovements(Field.BLACKSTONE);
			assertSame(moves[1][1], false);
			assertSame(moves[2][1], false);
			assertSame(moves[6][6], false);
			assertSame(moves[6][4], false);
			assertSame(moves[2][13], false);
			assertSame(moves[3][14], true);
			assertSame(moves[11][11], true);
		}
		catch (BadFieldException e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	public void isKO_WorkingSimpleTest()
	{
		if(!boardReal.makeMovement(1, 2, Field.BLACKSTONE, Field.WHITESTONE))
			fail();
		if(!boardReal.makeMovement(2, 1, Field.BLACKSTONE, Field.WHITESTONE))
			fail();
		if(!boardReal.makeMovement(2, 3, Field.BLACKSTONE, Field.WHITESTONE))
			fail();
		if(!boardReal.makeMovement(3, 1, Field.WHITESTONE, Field.BLACKSTONE))
			fail();
		if(!boardReal.makeMovement(3, 3, Field.WHITESTONE, Field.BLACKSTONE))
			fail();
		if(!boardReal.makeMovement(4, 2, Field.WHITESTONE, Field.BLACKSTONE))
			fail();
		if(!boardReal.makeMovement(3, 2, Field.BLACKSTONE, Field.WHITESTONE)) //kamień zabierany
			fail();
		if(!boardReal.makeMovement(2, 2, Field.WHITESTONE, Field.BLACKSTONE)) //ruch włączający zasadę KO
			fail();
		try
		{
			
			Field[][] board=boardReal.getBoardCopy();
			for(int i=1; i<5; i++)
			{
				System.out.println();
				for(int j=1; j<5; j++)
				{
					if(board[i][j]==Field.BLACKSTONE)
						System.out.print('B');
					else if(board[i][j]==Field.WHITESTONE)
						System.out.print('W');
					else
						System.out.print('-');
				}
			}
			System.out.println();
			if(board[3][2]!=Field.EMPTY) //sprawdzenie, czy kamień został odebrany
				fail();
			moves=boardReal.getPossibleMovements(Field.BLACKSTONE);
			assertSame(moves[2][1], false); // ruch niemożliwy przez zasadę KO, działający dla board[3][2]
			if(!boardReal.makeMovement(6, 6, Field.BLACKSTONE, Field.WHITESTONE))
				fail();
			if(!boardReal.makeMovement(5, 7, Field.WHITESTONE, Field.BLACKSTONE))
				fail();
			if(!boardReal.makeMovement(3, 2, Field.BLACKSTONE, Field.WHITESTONE)) //ruch znowu możliwy
				fail();
			board=boardReal.getBoardCopy();
			for(int i=1; i<5; i++)
			{
				System.out.println();
				for(int j=1; j<5; j++)
				{
					if(board[i][j]==Field.BLACKSTONE)
						System.out.print('B');
					else if(board[i][j]==Field.WHITESTONE)
						System.out.print('W');
					else
						System.out.print('-');
				}
			}
					
			if(board[2][2]!=Field.EMPTY) //sprawdzenie, czy kamień został odebrany
				fail();
			moves=boardReal.getPossibleMovements(Field.WHITESTONE);
			assertSame(moves[1][1], false); // ruch niemożliwy przez zasadę KO, działający tym razem dla board[2][2]
			
		}
		catch (BadFieldException e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	public void isKillingChainByMovesSimpleTest()
	{
		if(!boardReal.makeMovement(19, 7, Field.BLACKSTONE, Field.WHITESTONE))
			fail();
		if(!boardReal.makeMovement(18, 6, Field.BLACKSTONE, Field.WHITESTONE))
			fail();
		if(!boardReal.makeMovement(18, 5, Field.BLACKSTONE, Field.WHITESTONE))
			fail();
		if(!boardReal.makeMovement(19, 6, Field.WHITESTONE, Field.BLACKSTONE))
			fail();
		if(!boardReal.makeMovement(19, 5, Field.WHITESTONE, Field.BLACKSTONE))
			fail();
		if(!boardReal.makeMovement(18, 4, Field.WHITESTONE, Field.BLACKSTONE))
			fail();
		if(!boardReal.makeMovement(17, 5, Field.BLACKSTONE, Field.WHITESTONE))
			fail();
		if(!boardReal.makeMovement(17, 4, Field.WHITESTONE, Field.BLACKSTONE))
			fail();
		if(!boardReal.makeMovement(16, 4, Field.BLACKSTONE, Field.WHITESTONE))
			fail();
		if(!boardReal.makeMovement(17, 3, Field.BLACKSTONE, Field.WHITESTONE))
			fail();
		if(!boardReal.makeMovement(18, 3, Field.BLACKSTONE, Field.WHITESTONE))
			fail();
		if(!boardReal.makeMovement(19, 3, Field.WHITESTONE, Field.BLACKSTONE))
			fail();
		if(!boardReal.makeMovement(19, 2, Field.WHITESTONE, Field.BLACKSTONE))
			fail();
		if(!boardReal.makeMovement(19, 1, Field.WHITESTONE, Field.BLACKSTONE))
			fail();
		if(!boardReal.makeMovement(18, 1, Field.BLACKSTONE, Field.WHITESTONE))
			fail();
		if(!boardReal.makeMovement(18, 2, Field.BLACKSTONE, Field.WHITESTONE))
			fail();
		if(!boardReal.makeMovement(19, 4, Field.BLACKSTONE, Field.WHITESTONE)) //położenie kamyka usuwającego 3 łańcuchy przeciwnika
			fail();
		
		Field[][] board=boardReal.getBoardCopy();
		if(board[19][5]!=Field.EMPTY) //sprawdzenie, czy kamień został odebrany
			fail();
		if(board[19][6]!=Field.EMPTY) //sprawdzenie, czy kamień został odebrany
			fail();
		if(board[19][2]!=Field.EMPTY) //sprawdzenie, czy kamień został odebrany
			fail();
		if(board[17][4]!=Field.EMPTY) //sprawdzenie, czy kamień został odebrany
			fail();
	}
	
	@Test
	public void isGetPossibleMovementsReturningWellInSimpleSituationTest()
	{
		try
		{
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
			fail(e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			fail(e.getMessage());
		}
		catch (BadFieldException e)
		{
			fail(e.getMessage());
		}
		catch (SecurityException e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	public void isGetPossibleMovementsReturningWellInSimpleNoLibertiesTest()
	{

		try
		{
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
			fail(e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			fail(e.getMessage());
		}
		catch (BadFieldException e)
		{
			fail(e.getMessage());
		}
		catch (SecurityException e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	public void isGetPossibleMovementsReturningWellInChainNoLibertiesTest()
	{
		try
		{
			boardArray = (Field[][]) field.get(boardReal);
			boardArray[4][5]=Field.WHITESTONE;
			boardArray[3][5]=Field.WHITESTONE;
			boardArray[6][5]=Field.BLACKSTONE;
			boardArray[5][4]=Field.BLACKSTONE;
			boardArray[4][4]=Field.BLACKSTONE;
			boardArray[3][4]=Field.BLACKSTONE;
			boardArray[2][5]=Field.BLACKSTONE;
			boardArray[5][6]=Field.BLACKSTONE;
			boardArray[4][6]=Field.BLACKSTONE;
			boardArray[3][6]=Field.WHITESTONE;
			boardArray[2][6]=Field.BLACKSTONE;
			boardArray[3][7]=Field.BLACKSTONE;
			boardArray[15][5]=Field.WHITESTONE;
			field.set(boardReal, boardArray);
			moves=boardReal.getPossibleMovements(Field.WHITESTONE);
			Assert.assertEquals(false, moves[4][4]); // niemożliwe położenie kamienia w miejscu bez oddechów
			moves=boardReal.getPossibleMovements(Field.BLACKSTONE);
			Assert.assertEquals(true, moves[4][4]);
		}
		catch (IllegalArgumentException e)
		{
			fail(e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			fail(e.getMessage());
		}
		catch (BadFieldException e)
		{
			fail(e.getMessage());
		}
		catch (SecurityException e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	public void isGetPossibleMovementsReturningWellInChainNoLibertiesWithKillingConcurrentChainTest()
	{
		try
		{
			boardArray = (Field[][]) field.get(boardReal);
			boardArray[4][5]=Field.WHITESTONE;
			boardArray[3][5]=Field.WHITESTONE;	//  12345678
			boardArray[6][5]=Field.BLACKSTONE;	//1 --------
			boardArray[5][4]=Field.BLACKSTONE;	//2 ----BB--
			boardArray[4][4]=Field.BLACKSTONE;	//3 ---BWWB-
			boardArray[3][4]=Field.BLACKSTONE;	//4 ---BWB--
			boardArray[2][5]=Field.BLACKSTONE;	//5 ---B_B--
			boardArray[5][6]=Field.BLACKSTONE;	//6 ---WBW--
			boardArray[4][6]=Field.BLACKSTONE;	//7 ----W---
			boardArray[3][6]=Field.WHITESTONE;	//8 --------
			boardArray[2][6]=Field.BLACKSTONE;
			boardArray[3][7]=Field.BLACKSTONE;
			boardArray[6][4]=Field.WHITESTONE;
			boardArray[6][6]=Field.WHITESTONE;
			boardArray[7][5]=Field.WHITESTONE;
			field.set(boardReal, boardArray);
			moves=boardReal.getPossibleMovements(Field.WHITESTONE);
			Assert.assertEquals(true, moves[4][4]); // możliwe położenie kamienia, kiedy zabija się łańcuch przeciwnika
			moves=boardReal.getPossibleMovements(Field.BLACKSTONE);
			Assert.assertEquals(true, moves[4][4]);
		}
		catch (IllegalArgumentException e)
		{
			fail(e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			fail(e.getMessage());
		}
		catch (BadFieldException e)
		{
			fail(e.getMessage());
		}
		catch (SecurityException e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	public void isGetPossibleMovementsReturningFalseOnKOFieldTest()
	{
		try
		{
			java.lang.reflect.Field field1=boardReal.getClass().getDeclaredField("xKO");
			java.lang.reflect.Field field2=boardReal.getClass().getDeclaredField("yKO");
			field1.setAccessible(true);
			field2.setAccessible(true);
			int x, y;
			x = 5;
			y = 5;
			field1.set(boardReal, x);
			field2.set(boardReal, y);
			moves=boardReal.getPossibleMovements(Field.WHITESTONE);
			Assert.assertEquals(false, moves[4][4]); // możliwe położenie kamienia, kiedy zabija się łańcuch przeciwnika
		}
		catch (IllegalArgumentException e)
		{
			fail(e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			fail(e.getMessage());
		}
		catch (BadFieldException e)
		{
			fail(e.getMessage());
		}
		catch (SecurityException e)
		{
			fail(e.getMessage());
		}
		catch (NoSuchFieldException e)
		{
			fail(e.getMessage());
		}
	}
}
