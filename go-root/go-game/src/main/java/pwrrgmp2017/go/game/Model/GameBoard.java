package pwrrgmp2017.go.game.Model;

import pwrrgmp2017.go.game.Exception.KOException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;

public class GameBoard
{
	private Field[][] board;
	private int size;
	int xKO, yKO;

	enum Field
	{
		WHITESTONE, BLACKSTONE, EMPTY, WHITETERRITORY, BLACKTERRITORY, WALL;
	}

	GameBoard(int size)
	{
		this.size= size;
		this.board= new Field[size+2][size+2];
		for(int i= 1; i<size+1; i++)
		{
			for(int j= 1; j<size+1; j++)
			{
				this.board[i][j]= Field.EMPTY;
			}
		}
		for(int i= 0; i<size+2; i++)
		{
			this.board[i][0]= Field.WALL;
			this.board[i][size+1]= Field.WALL;
			this.board[0][i]= Field.WALL;
			this.board[size+1][i]= Field.WALL;
		}
		xKO= 0;
		yKO= 0;
	}

	int getSize()
	{
		return size;
	}

	boolean[][] getPossibleMovements(Field playerField) throws BadFieldException
	{
		Field concurField;
		switch(playerField)
		{
		case WHITESTONE:
			concurField= Field.BLACKSTONE;
			break;
		case BLACKSTONE:
			concurField= Field.WHITESTONE;
			break;
		default:
			throw new BadFieldException();
		}
		boolean[][] possibleMovements= new boolean[size][size];
		for(int i= 1; i<size+1; i++)
		{
			for(int j= 1; j<size+1; j++)
			{
				if(board[i][j]==Field.EMPTY)
					possibleMovements[i-1][j-1]= true;
				else
				{
					possibleMovements[i-1][j-1]= false;
					continue;
				}
				if(board[i+1][j]==playerField || board[i-1][j]==playerField || board[i][j+1]==playerField || board[i][j-1]==playerField)
				{
					
					if(isChainKilled(playerField, concurField, i, j))
					{
						
					}
					else
					{
						
					}
				}
				else if( (board[i+1][j]==concurField || board[i+1][j]==Field.WALL) //sprawdzenie czy kamyk jest zamknięty
				        && (board[i][j+1]==concurField || board[i][j+1]==Field.WALL)
				        && (board[i][j-1]==concurField || board[i][j-1]==Field.WALL)
				        && (board[i-1][j]==concurField || board[i-1][j]==Field.WALL))
				{
					try
					{
						if(!isKillingOther(concurField, i, j))
							possibleMovements[i-1][j-1]=true;
					}
					catch(KOException e)
					{
						possibleMovements[i-1][j-1]=false;
					}
				}
				else
				{
				}
			}
		}
		return possibleMovements;
	}

	private boolean isChainKilled(Field playerField, Field concurField, int i, int j)
	{
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isChainKilled(boolean[][] chain)
	{
		
		return false;
	}

	private boolean isKillingOther(Field concur, int x, int y) throws KOException
	{
		if(board[x+1][y]==concur)
			if(isKilling(x+1, y))
				return true;
		if(board[x-1][y]==concur)
			if(isKilling(x-1, y))
				return true;
		if(board[x][y+1]==concur)
			if(isKilling(x, y+1))
				return true;
		if(board[x][y-1]==concur)
			if(isKilling(x, y-1))
				return true;
		return false;
	}

	boolean isKilling(int x, int y) throws KOException
	{
		Field concurField, playerField;
		switch(board[x][y])
		{
		case WHITESTONE:
			concurField= Field.BLACKSTONE;
			playerField= Field.WHITESTONE;
			break;
		case BLACKSTONE:
			concurField= Field.WHITESTONE;
			playerField= Field.BLACKSTONE;
			break;
		default:
			return false;
		}
		if( (board[x+1][y]==concurField || board[x+1][y]==Field.WALL) //sprawdzenie oddechów danego pola
		        && (board[x][y+1]==concurField || board[x][y+1]==Field.WALL)
		        && (board[x][y-1]==concurField || board[x][y-1]==Field.WALL)
		        && (board[x-1][y]==concurField || board[x-1][y]==Field.WALL))
		{
			
		}
		return false;
	}
}
