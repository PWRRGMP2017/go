package pwrrgmp2017.go.game.Model;

import java.util.Arrays;

import pwrrgmp2017.go.game.Exception.KOException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;

public class GameBoard
{
	private Field[][] board;
	private int size;
	private int xKO, yKO;
	private boolean[][] chain; //zachłanna inicjalizacja, w celu zmniejszenia liczby ciągłego inicjowania nowych tablic

	public enum Field
	{
		WHITESTONE, BLACKSTONE, EMPTY, WHITETERRITORY, BLACKTERRITORY, WALL;
	}

	GameBoard(int size)
	{
		this.size= size;
		this.board= new Field[size+2][size+2];
		this.chain= new boolean[size][size];
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

	public int getSize()
	{
		return size;
	}

	public boolean[][] getPossibleMovements(Field playerField) throws BadFieldException
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
				if(board[i][j]!=Field.EMPTY) //jeśli pole jest puste
				{
					possibleMovements[i-1][j-1]= false;
					continue;
				}
				board[i][j]=playerField; //zmienna musi byc na moment zmieniona, by sprawdzic zabicie łańcuchów
				if(!isChainKilled(playerField, concurField, i, j)) //jeśli łańcuch nie będzie zabity
				{
					possibleMovements[i-1][j-1]=true;
				}
				else //jeśli łańcuch gracza będzie zabity odbywa się sprawdzenie, czy w ten sposób któryś łańcuch przeciwnika będzie zabity
				{
					possibleMovements[i-1][j-1]=false;
					try
					{
						if(board[i+1][j]==concurField)
						{
							if(isChainKilled(concurField, playerField, i, j))
								possibleMovements[i-1][j-1]=true;
							else if(board[i-1][j]==concurField )
							{
								if(isChainKilled(concurField, playerField, i, j))
									possibleMovements[i-1][j-1]=true;
								else if(board[i][j+1]==concurField)
								{
									if(isChainKilled(concurField, playerField, i, j))
										possibleMovements[i-1][j-1]=true;
									else if(board[i][j-1]==concurField)
									{
										if(isChainKilled(concurField, playerField, i, j))
											possibleMovements[i-1][j-1]=true;
									}
								}
							}
						}
					}
					catch(KOException e)
					{
						possibleMovements[i-1][j-1]=false;
					}
				}
				board[i][j]=Field.EMPTY;
			}
		}
		return possibleMovements;
	}

	private boolean isChainKilled(Field playerField, Field concurField, int i, int j) throws KOException
	{
		if(board[i][j]==concurField)
			return false;
		for(int a=0; a<size; a++)
			Arrays.fill(chain[a], false);
		chain[i-1][j-1]=true;
		isChainKilledRecursive(playerField, concurField, i, j);
		return false;
	}

	private boolean isChainKilledRecursive(Field playerField, Field concurField, int i, int j) //rekurencyjna metoda
	{
		if(board[i-1][j]!=Field.WALL && !chain[i-2][j-1])
		{
			switch(board[i-1][j])
			{
			case WHITESTONE:
				if(playerField==Field.WHITESTONE)
				{
					chain[i-2][j-1]=true;
					if(!isChainKilledRecursive(playerField, concurField, i-1, j))
						return false;
				}
				break;
			case BLACKSTONE:
				if(playerField==Field.BLACKSTONE)
				{
					chain[i-2][j-1]=true;
					if(!isChainKilledRecursive(playerField, concurField, i-1, j))
						return false;
				}
				break;
			case WALL:
				break;
			default:
				return false;
			}
		}
		if(board[i][j-1]!=Field.WALL && !chain[i-1][j-2])
		{
			switch(board[i][j-1])
			{
			case WHITESTONE:
				if(playerField==Field.WHITESTONE)
				{
					chain[i-1][j-2]=true;
					if(!isChainKilledRecursive(playerField, concurField, i, j-1))
						return false;
				}
				break;
			case BLACKSTONE:
				if(playerField==Field.BLACKSTONE)
				{
					chain[i-1][j-2]=true;
					if(!isChainKilledRecursive(playerField, concurField, i, j-1))
						return false;
				}
				break;
			case WALL:
				break;
			default:
				return false;
			}
		}
		if(board[i+1][j]!=Field.WALL && !chain[i][j-1])
		{
			switch(board[i+1][j])
			{
			case WHITESTONE:
				if(playerField==Field.WHITESTONE)
				{
					chain[i][j-1]=true;
					if(!isChainKilledRecursive(playerField, concurField, i+1, j))
						return false;
				}
				break;
			case BLACKSTONE:
				if(playerField==Field.BLACKSTONE)
				{
					chain[i][j-1]=true;
					if(!isChainKilledRecursive(playerField, concurField, i+1, j))
						return false;
				}
				break;
			case WALL:
				break;
			default:
				return false;
			}
		}
		if(board[i][j+1]!=Field.WALL && !chain[i-1][j])
		{
			switch(board[i][j+1])
			{
			case WHITESTONE:
				if(playerField==Field.WHITESTONE)
				{
					chain[i-1][j]=true;
					if(!isChainKilledRecursive(playerField, concurField, i, j+1))
						return false;
				}
				break;
			case BLACKSTONE:
				if(playerField==Field.BLACKSTONE)
				{
					chain[i-1][j]=true;
					if(!isChainKilledRecursive(playerField, concurField, i, j+1))
						return false;
				}
				break;
			case WALL:
				break;
			default:
				return false;
			}
		}
		return true;
	}
}
