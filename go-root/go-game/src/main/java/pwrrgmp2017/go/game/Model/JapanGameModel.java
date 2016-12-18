package pwrrgmp2017.go.game.Model;

import java.util.Arrays;

import pwrrgmp2017.go.game.Exception.TheSameChainException;
import pwrrgmp2017.go.game.Model.GameBoard.Field;

public class JapanGameModel extends GameModel
{
	Field[][] board, boardReturn;
	boolean[][] chain, friendChain;
	int emptyFieldsFriendChain;

	public JapanGameModel(GameBoard board, float komi)
	{
		super(board, komi);
		chain= new boolean[board.getSize()-2][board.getSize()-2];
		friendChain= new boolean[board.getSize()-2][board.getSize()-2];
	}

	@Override
	public float calculateScore()
	{
		@SuppressWarnings("unused")
		Field[][] board=super.getBoardCopy();
		
		return 0;
	}

	@Override
	public Field[][] getPossibleTerritory()
	{
		board=super.getBoardCopy();
		boardReturn=board.clone();
		for(int i=1; i<board.length-1; i++) //zaznaczenie wszystkich martwych łańcuchów
			for(int j=1; j<board.length-1; j++)
			{
				if(checkIfChainIsDead(i, j)<10)	
					addDeadChainToTerritories();
			}
		
		Field field;
		for(int i=1; i<board.length-1; i++) //zaznaczanie oczywistych terytoriów 
			for(int j=1; j<board.length-1; j++)
			{
				if(boardReturn[i][j]!=Field.EMPTY) // gwarancja, że terytorium nie będzie sprawdzane 2 razy
					continue;
				
				for(int a=0; a<chain.length; a++)
					Arrays.fill(chain[a], false);
				
				field=checkIfAreaIsTerritory(i, j);
				
				if(field != Field.WALL)	
					addWholeArea(field);
				else
					addWholeArea(Field.NONETERRITORY);
			}
		for(int i=1; i<board.length-1; i++) // zaznaczenie terytoriów jako wpływów w na polach
			for(int j=1; j<board.length-1; j++)
			{
				if(boardReturn[i][j]!=Field.NONETERRITORY)
					continue;
				checkNoneTerritory(i, j);
			}
		
		return boardReturn;
	}

	private void checkNoneTerritory(int i, int j)
	{
		int territoryPoints=0;
		territoryPoints+=30*takeTerritoryPoints(i+1, j);
		territoryPoints+=30*takeTerritoryPoints(i-1, j);
		territoryPoints+=30*takeTerritoryPoints(i, j+1);
		territoryPoints+=30*takeTerritoryPoints(i, j-1);
		territoryPoints+=11*takeTerritoryPoints(i+1, j+1);
		territoryPoints+=11*takeTerritoryPoints(i-1, j-1);
		territoryPoints+=11*takeTerritoryPoints(i+1, j-1);
		territoryPoints+=11*takeTerritoryPoints(i-1, j+1);
		territoryPoints+=5*takeTerritoryPoints(i+2, j);
		territoryPoints+=5*takeTerritoryPoints(i-2, j);
		territoryPoints+=5*takeTerritoryPoints(i, j+2);
		territoryPoints+=5*takeTerritoryPoints(i, j-2);
		territoryPoints+=2*takeTerritoryPoints(i+1, j+2);
		territoryPoints+=2*takeTerritoryPoints(i+1, j-2);
		territoryPoints+=2*takeTerritoryPoints(i-1, j+2);
		territoryPoints+=2*takeTerritoryPoints(i-1, j-1);
		territoryPoints+=2*takeTerritoryPoints(i+2, j+1);
		territoryPoints+=2*takeTerritoryPoints(i+2, j-1);
		territoryPoints+=2*takeTerritoryPoints(i-2, j+1);
		territoryPoints+=2*takeTerritoryPoints(i-2, j-1);
		if(territoryPoints>100)
			boardReturn[i][j]=Field.BLACKTERRITORY;
		else if(territoryPoints<-100)
			boardReturn[i][j]=Field.WHITETERRITORY;
	}
	

	private int takeTerritoryPoints(int i, int j)
	{
		if(i<1 || i>board.length-1 || j<1 || j>board.length-1)
			return 0;
		
		switch(board[i][j])
		{
		case BLACKSTONE:
			return 3;
		case WHITESTONE:
			return -3;
		case DEADBLACK:
			return -1;
		case DEADWHITE:
			return 1;
		default:
			return 0;
		}
	}
		
		

	private void addWholeArea(Field field) //Dodaje w pełni zajęte terytoria do zwracanej planszy
	{
		for(int i=0; i<chain.length; i++)
			for(int j=0; j<chain.length; j++)
			{
				if(chain[i][j]==true)
					boardReturn[i+1][j+1]=field;
			}
	}
	

	private Field checkIfAreaIsTerritory(int i, int j)
	{
		chain[i-1][j-1]=true;
		Field field1=checkIfAreaIsTerritoryRecursive(i, j);
		Field field2=checkIfAreaIsTerritoryRecursive(i, j);
		Field field3=checkIfAreaIsTerritoryRecursive(i, j);
		Field field4=checkIfAreaIsTerritoryRecursive(i, j);
			
		if( (field1==Field.BLACKSTONE || field1==Field.EMPTY)
			&& (field2==Field.BLACKSTONE || field2==Field.EMPTY)
			&& (field3==Field.BLACKSTONE || field3==Field.EMPTY)
			&& (field4==Field.BLACKSTONE || field4==Field.EMPTY))
			return Field.BLACKSTONE;
		if( (field1==Field.WHITESTONE || field1==Field.EMPTY)
				&& (field2==Field.WHITESTONE || field2==Field.EMPTY)
				&& (field3==Field.WHITESTONE || field3==Field.EMPTY)
				&& (field4==Field.WHITESTONE || field4==Field.EMPTY))
				return Field.WHITESTONE;
		
		return Field.WALL; //zwrócenie WALL, jeśli nie można jednoznacznie określic terytorium
	}
	
	private Field checkIfAreaIsTerritoryRecursive(int i, int j)
	{
		switch(board[i][j])
		{
		case BLACKSTONE:
		case DEADWHITE:
			return Field.BLACKSTONE;
		case WHITESTONE:
		case DEADBLACK:
			return Field.WHITESTONE;
		case EMPTY:
			if(chain[i-1][j-1]==true)
				return Field.EMPTY;
			else
				return checkIfAreaIsTerritory(i, j);
		default:
			return Field.EMPTY;
		}
	}
	

	private int checkIfChainIsDead(int i, int j) //zwraca ilośc całkowitych "punktów życia" łańcucha
	{
		Field field, concur;
		if(board[i][j]!=Field.BLACKSTONE && board[i][j]!=Field.WHITESTONE)
			return 100;
		field=board[i][j];
		for(int a=0; a<chain.length; a++)
			Arrays.fill(chain[a], false);
		
		if(field==Field.BLACKSTONE)
			concur=Field.WHITESTONE;
		else
			concur=Field.BLACKSTONE;
		int lifePoints=checkIfChainIsDeadRecursive(i, j, field, concur);
		lifePoints+=(checkEyes())*6;	
		
		return lifePoints;
	}
	
	private int checkEyes() //sprawdza czy istnieją oka wewnątrz danego łańcucha
	{
		int eyes=0;
		for(int i=1; i<chain.length-1; i++) 
		{
			for(int j=1; j<chain.length-1; j++)
			{
				if(chain[i][j]==false)//sprawdzenie pojedyńczych oczu
				{
					if(chain[i+1][j]==true && chain[i-1][j]==true && chain[i][j+1]==true && chain[i][j-1]==true)
						eyes++;
				}
				if(i!=chain.length-2) //sprawdzenie podwójnych oczu
					if(chain[i][j]==false && chain[i+1][j]==false)
						if(chain[i-1][j]==true && chain[i+2][j]==true && chain[i][j-1]==true && chain[i][j+1]==true && chain[i+1][j+1]==true && chain[i+1][j-1]==true)
							eyes++;
				if(j!=chain.length-2)
					if(chain[i][j]==false && chain[i][j+1]==false)
						if(chain[i][j-1]==true && chain[i][j+2]==true && chain[i-1][j]==true && chain[i+1][j]==true && chain[i+1][j+1]==true && chain[i-1][j+1]==true)
							eyes++;
			}
		}
		return eyes;
	}
	

	private int checkIfChainIsDeadRecursive(int i, int j, Field field, Field concur) //Sprawdza rekurencyjnie czy łańcuch można uznac za niemal martwy
	{
		chain[i-1][j-1]=true;
		int lifePoints=0;
		lifePoints+=checkIfChainIsDeadRecursiveTwo(i-1, j, field, concur);
		lifePoints+=checkIfChainIsDeadRecursiveTwo(i+1, j, field, concur);
		lifePoints+=checkIfChainIsDeadRecursiveTwo(i, j+1, field, concur);
		lifePoints+=checkIfChainIsDeadRecursiveTwo(i, j-1, field, concur);
		
		return lifePoints;
	}
	
	
	private int checkIfChainIsDeadRecursiveTwo(int i, int j, Field field, Field concur) //Dopełnia pośrednią rekurencję dla checkIfChainIsDeadRecursive
	{
		switch(board[i][j])
		{
		case BLACKSTONE:
			if(field==Field.BLACKSTONE)
			{
				if(chain[i-1][j-1]==true)
					return 0;
				else
					return checkIfChainIsDeadRecursive(i, j, field, concur);
			}
			else
			{
				return 0;
			}
		case WHITESTONE:
			if(field==Field.WHITESTONE)
			{
				if(chain[i-1][j-1]==true)
					return 0;
				else
					return checkIfChainIsDeadRecursive(i, j, field, concur);
			}
			else
			{
				return 0;
			}
		case EMPTY:
			int lifePoints=0;
			lifePoints+=checkEmptyField(i+1, j, field, concur);
			lifePoints+=checkEmptyField(i-1, j, field, concur);
			lifePoints+=checkEmptyField(i, j+1, field, concur);
			lifePoints+=checkEmptyField(i, j-1, field, concur);
			return lifePoints;
		default:
			return 0;
		}
	}
	

	private int checkEmptyField(int i, int j, Field field, Field concur) //Sprawdza ilośc "punktów życiowych" dla oddechu w łańcuchu
	{
		int lifePoints=0;
		if(board[i][j]==Field.EMPTY)
		{
			if(board[i+1][j]==Field.EMPTY)
				lifePoints++;
			if(board[i-1][j]==Field.EMPTY)
				lifePoints++;
			if(board[i][j+1]==Field.EMPTY)
				lifePoints++;
			if(board[i][j-1]==Field.EMPTY)
				lifePoints++;
		}
		else if(board[i][j]==field && chain[i-1][j-1])
		{
			if(!checkFriendChain(i, j, field, concur))
				lifePoints+=3;
		}
		return lifePoints;
	}

	
	private boolean checkFriendChain(int i, int j, Field field, Field concur) //Sprawdza czy przyjazny łańcuch przy oddechu ma inne oddechy
	{
		for(int a=0; a<chain.length; a++)
			Arrays.fill(friendChain[a], false);
		emptyFieldsFriendChain=0;
		try
		{
			checkFriendChainRecursive(i, j, field, concur);
		}
		catch (TheSameChainException e)
		{
			return false;
		}
		if(emptyFieldsFriendChain>2)
			return true;
		else
			return false;
	}
	

	private void checkFriendChainRecursive(int i, int j, Field field, Field concur) throws TheSameChainException //Rekurencyjna metoda wykorzystywana przez checkFriendChain
	{
		friendChain[i-1][j-1]=true;
		if(chain[i-2][j-1]==true || chain[i-1][j-2]==true || chain[i][j-1]==true || chain[i-1][j]==true)
			throw new TheSameChainException();
		
		if(board[i+1][j]==field && !friendChain[i][j-1])
			checkFriendChainRecursive(i+1, j, field, concur);
		else if(board[i][j]==Field.EMPTY)
			emptyFieldsFriendChain++;
		
		if(board[i-1][j]==field && !friendChain[i-2][j-1])
			checkFriendChainRecursive(i-1, j, field, concur);
		else if(board[i][j]==Field.EMPTY)
			emptyFieldsFriendChain++;
		
		if(board[i][j+1]==field && !friendChain[i-1][j])
			checkFriendChainRecursive(i, j+1, field, concur);
		else if(board[i][j]==Field.EMPTY)
			emptyFieldsFriendChain++;
		
		if(board[i][j-1]==field && !friendChain[i-1][j-2])
			checkFriendChainRecursive(i, j-1, field, concur);
		else if(board[i][j]==Field.EMPTY)
			emptyFieldsFriendChain++;
	}

	
	private void addDeadChainToTerritories() //Dodaje prawie martwe łańcuchy do zwracanej planszy (board)
	{
		for(int i=0; i<chain.length; i++)
			for(int j=0; j<chain.length; j++)
				if(chain[i][j]==true)
				{
					if(board[i+1][j+1]==Field.BLACKSTONE)
						board[i+1][j+1]=Field.DEADBLACK;
					if(board[i+1][j+1]==Field.WHITESTONE)
						board[i+1][j+1]=Field.DEADWHITE;
				}
	}
	
}
