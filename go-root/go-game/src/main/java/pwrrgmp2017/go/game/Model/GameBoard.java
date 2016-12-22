package pwrrgmp2017.go.game.Model;

import java.util.Arrays;

import pwrrgmp2017.go.game.Exceptions.BadFieldException;

public class GameBoard
{
	private Field[][] board;
	private int size;
	private int xKO, yKO;
	private boolean[][] chain; //zachłanna inicjalizacja, w celu zmniejszenia liczby ciągłego deklarowania nowych tablic
	int whiteCaptives, blackCaptives;

	public enum Field
	{
		WHITESTONE, BLACKSTONE, EMPTY, WHITETERRITORY, BLACKTERRITORY, WALL, NONETERRITORY, DEADWHITE, DEADBLACK;
	}

	public GameBoard(int size)
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
		whiteCaptives=0;
		blackCaptives=0;
	}

	public int getSize()
	{
		return size;
	}
	
	public boolean makeMovement(int i, int j, Field playerField, Field concurField)
	{
		if(!isMovePossible(playerField, concurField, i, j))
			return false;
		
		board[i][j]=playerField;
		
		boolean isKO;
		isKO=tryKO(i, j, playerField, concurField);
		
		if(board[i+1][j]==concurField)
		{
			if(isChainKilled(concurField, playerField, i+1, j))
				killChain(concurField, i+1, j);
		}
		if(board[i-1][j]==concurField)
		{
			if(isChainKilled(concurField, playerField, i-1, j))
				killChain(concurField, i-1, j);
		}
		if(board[i][j+1]==concurField)
		{
			if(isChainKilled(concurField, playerField, i, j+1))
				killChain(concurField, i, j+1);
		}
		if(board[i][j-1]==concurField)
		{
			if(isChainKilled(concurField, playerField, i, j-1))
				killChain(concurField, i, j-1);
		}
		if(!isKO)
		{
			xKO=0;
			yKO=0;
		}
		return true;
	}
	
	private void killChain(Field concurField, int i, int j)
	{
		board[i][j]=Field.EMPTY;
		addCaptive(concurField);
		if(concurField==board[i+1][j])
		{
			killChain(concurField, i+1, j);
		}
		if(concurField==board[i-1][j])
		{
			killChain(concurField, i-1, j);
		}
		if(concurField==board[i][j+1])
		{
			killChain(concurField, i, j+1);
		}
		if(concurField==board[i][j-1])
		{
			killChain(concurField, i, j-1);
		}
	}

	private void addCaptive(Field field) //metoda dodająca do liczby zabranych kamieni kolejny kamień
	{
		if(field==Field.BLACKSTONE)
			blackCaptives++;
		else if(field==Field.WHITESTONE)
			whiteCaptives++;
	}
	
	private boolean tryKO(int i, int j, Field playerField, Field concurField)
	{
		if((board[i+1][j]==concurField || Field.WALL==board[i+1][j])  && (board[i-1][j]==concurField || Field.WALL==board[i-1][j])
				&& (board[i][j+1]==concurField || Field.WALL==board[i][j+1]) && (board[i][j-1]==concurField || Field.WALL==board[i][j-1]))
		{
			int KO_Stones=0, KOi=0, KOj=0;
			if(i+2<board.length)
				if((board[i+1][j+1]==playerField || Field.WALL==board[i+1][j+1]) 
						&& (board[i+1][j-1]==playerField || Field.WALL==board[i+1][j-1]) 
						&& (board[i+2][j]==playerField || Field.WALL==board[i+2][j]))
				{
					KO_Stones++;
					KOi=i+1;
					KOj=j;
				}
			if(i-2>=0)
				if((board[i-1][j+1]==playerField || Field.WALL==board[i-1][j+1]) 
						&& (board[i-1][j-1]==playerField || Field.WALL==board[i-1][j-1]) 
						&& (board[i-2][j]==playerField || Field.WALL==board[i-2][j]))
				{
					KO_Stones++;
					KOi=i-1;
					KOj=j;
				}
			if(j+2<board.length)
				if((board[i-1][j+1]==playerField || Field.WALL==board[i-1][j+1]) 
						&& (board[i+1][j+1]==playerField || Field.WALL==board[i+1][j+1]) 
						&& (board[i][j+2]==playerField || Field.WALL==board[i][j+2]))
				{
					KO_Stones++;
					KOi=i;
					KOj=j+1;
				}
			if(j-2>=0)
				if((board[i-1][j-1]==playerField || Field.WALL==board[i-1][j-1]) 
						&& (board[i+1][j-1]==playerField || Field.WALL==board[i+1][j-1]) 
						&& (board[i][j-2]==playerField || Field.WALL==board[i][j-2]))
				{
					KO_Stones++;
					KOi=i;
					KOj=j-1;
				}
			if(KO_Stones==1)
			{
				xKO=KOi;
				yKO=KOj;
				board[KOi][KOj]=Field.EMPTY;
				addCaptive(concurField);
				return true;
			}
				
		}
		return false;
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
				possibleMovements[i-1][j-1]=isMovePossible(playerField, concurField, i, j);
			}
		}
		return possibleMovements;
	}
	
	private boolean isMovePossible(Field playerField, Field concurField, int i, int j)
	{
		boolean move;
		if(board[i][j]!=Field.EMPTY) //jeśli pole jest puste
		{
			return false;
		}
		if(i==xKO && j==yKO) //jeśli pole powoduje KO
		{
			return false;
		}
		board[i][j]=playerField; //zmienna musi byc na moment zmieniona, by sprawdzic zabicie łańcuchów
		if(!isChainKilled(playerField, concurField, i, j)) //jeśli łańcuch nie będzie zabity
		{
			board[i][j]=Field.EMPTY;
			return true;
		}
		else //jeśli łańcuch gracza będzie zabity odbywa się sprawdzenie, czy w ten sposób któryś łańcuch przeciwnika będzie zabity
		{
			move=false;
			if(board[i+1][j]==concurField)
			{
				if(isChainKilled(concurField, playerField, i+1, j))
					move=true;
			}
			if(board[i-1][j]==concurField )
			{
				if(isChainKilled(concurField, playerField, i-1, j))
					move=true;
			}
			if(board[i][j+1]==concurField)
			{
				if(isChainKilled(concurField, playerField, i, j+1))
					move=true;
			}
			if(board[i][j-1]==concurField)
			{
				if(isChainKilled(concurField, playerField, i, j-1))
					move=true;
			}
		}
		board[i][j]=Field.EMPTY;
		return move;
	}

	private boolean isChainKilled(Field playerField, Field concurField, int i, int j)
	{
		if(board[i][j]==concurField)
			return false;
		for(int a=0; a<size; a++)
			Arrays.fill(chain[a], false);
		chain[i-1][j-1]=true;
		return isChainKilledRecursive(playerField, concurField, i, j);
	}

	private boolean isChainKilledRecursive(Field playerField, Field concurField, int i, int j) //rekurencyjna metoda
	{
		if(board[i-1][j]!=Field.WALL)
		{
			if(!isChainKilledRecursiveTwo(playerField, concurField, i-1, j))
				return false;
		}
		if(board[i][j-1]!=Field.WALL)
		{
			if(!isChainKilledRecursiveTwo(playerField, concurField, i, j-1))
				return false;
		}
		if(board[i+1][j]!=Field.WALL)
		{
			if(!isChainKilledRecursiveTwo(playerField, concurField, i+1, j))
				return false;
		}
		if(board[i][j+1]!=Field.WALL)
		{
			if(!isChainKilledRecursiveTwo(playerField, concurField, i, j+1))
				return false;
		}
		return true;
	}
	private boolean isChainKilledRecursiveTwo(Field playerField, Field concurField, int i, int j)
	{
		if(!chain[i-1][j-1])
		{
			switch(board[i][j])
			{
			case WHITESTONE:
				if(playerField==Field.WHITESTONE)
				{
					chain[i-1][j-1]=true;
					if(!isChainKilledRecursive(playerField, concurField, i, j))
						return false;
				}
				break;
			case BLACKSTONE:
				if(playerField==Field.BLACKSTONE)
				{
					chain[i-1][j-1]=true;
					if(!isChainKilledRecursive(playerField, concurField, i, j))
						return false;
				}
				break;
			default:
				return false;
			}
		}
		return true;
	}

	public Field[][] getBoardCopy()
	{
		Field [][] cloneBoard= new Field[board.length][];
		for(int i=0; i<board.length; i++)
			cloneBoard[i]=board[i].clone();
		return cloneBoard;
	}

	public int getBlackCaptives()
	{
		return blackCaptives;
	}
	
	public int getWhiteCaptives()
	{
		return whiteCaptives;
	}
}
