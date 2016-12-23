package pwrrgmp2017.go.game.Model;

import java.util.Arrays;

import pwrrgmp2017.go.game.Exception.TheSameChainException;
import pwrrgmp2017.go.game.Model.GameBoard.Field;

/**
 * Class which have implemented methods for calculating Score
 * @author Robert Gawlik
 *
 */
public class JapanGameModel extends GameModel
{
	/**Boards uses by algorithms */
	Field[][] boardCopy, boardReturn; 
	/**Arrays which shows current chain */
	boolean[][] chain, friendChain;
	/**Number of briefes of friend chain. Is using in algorithms */
	int emptyFieldsFriendChain;
	
	/**
	 * Constructor of the class
	 * @param board Board which model uses
	 * @param komi Value of komi
	 */
	public JapanGameModel(GameBoard board, float komi)
	{
		super(board, komi);
		chain= new boolean[board.getSize()][board.getSize()];
		friendChain= new boolean[board.getSize()][board.getSize()];
	}

	/**
	 * Method which calculates the score of current situation
	 */
	@Override
	public float calculateScore()
	{
		Field[][] territory=this.getPossibleTerritory();
		float points=0; //ujemne dla białego, dodatnie dla czarnego
		for(int i=1; i<territory.length-1; i++)
		{
			for(int j=1; j<territory.length-1; j++)
			{
				switch(territory[i][j])
				{
				case BLACKTERRITORY:
					points++;
					break;
				case WHITETERRITORY:
					points--;
					break;
				case DEADBLACK:
					points-=2;
					break;
				case DEADWHITE:
					points+=2;
					break;
				default:
				}
			}
		}
		points-=super.getKomi();
		points-=super.getBlackCaptives();
		points+=super.getWhiteCaptives();
		return points;
	}
	
	/**
	 * Method which calculates score of situation in parametre
	 */
	@Override
	public float calculateScore(Field[][] territory)
	{
		float points=0; //ujemne dla białego, dodatnie dla czarnego
		for(int i=1; i<territory.length-1; i++)
		{
			for(int j=1; j<territory.length-1; j++)
			{
				switch(territory[i][j])
				{
				case BLACKTERRITORY:
					points++;
					break;
				case WHITETERRITORY:
					points--;
					break;
				case DEADBLACK:
					points-=2;
					break;
				case DEADWHITE:
					points+=2;
					break;
				default:
				}
			}
		}
		points-=super.getKomi();
		points-=super.getBlackCaptives();
		points+=super.getWhiteCaptives();
		return points;
	}

	/**
	 * Method which returns possible territory
	 */
	@Override
	public Field[][] getPossibleTerritory()
	{
		boardCopy=super.getBoardCopy();
		boardReturn=boardCopy.clone();
		for(int i=1; i<boardCopy.length-1; i++) //zaznaczenie wszystkich martwych łańcuchów
			for(int j=1; j<boardCopy.length-1; j++)
			{
				if(checkIfChainIsDead(i, j)<10)	
					addDeadChainToTerritories();
			}
		
		Field field;
		for(int i=1; i<boardCopy.length-1; i++) //zaznaczanie oczywistych terytoriów 
			for(int j=1; j<boardCopy.length-1; j++)
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
		for(int i=1; i<boardCopy.length-1; i++) // zaznaczenie terytoriów jako wpływów w na polach
			for(int j=1; j<boardCopy.length-1; j++)
			{
				if(boardReturn[i][j]!=Field.NONETERRITORY)
					continue;
				checkNoneTerritory(i, j);
			}
		
		return boardReturn;
	}

	/**
	 * Method which checks if the field should be a territory
	 * of the player and sets it
	 * @param i first attribute position
	 * @param j second attribute position
	 */
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
		if(territoryPoints>30)
			boardReturn[i][j]=Field.BLACKTERRITORY;
		else if(territoryPoints<-30)
			boardReturn[i][j]=Field.WHITETERRITORY;
	}
	
	/**
	 * Method which returns territory points of current Field
	 * @param i first attribute position
	 * @param j second attribute position
	 * @return territory points
	 */
	private int takeTerritoryPoints(int i, int j)
	{
		if(i<1 || i>boardCopy.length-1 || j<1 || j>boardCopy.length-1)
			return 0;
		
		switch(boardCopy[i][j])
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
	
	/**
	 * Makes chain (area) a territory of one player
	 * @param field Colour of player
	 */
	private void addWholeArea(Field field) //Dodaje w pełni zajęte terytoria do zwracanej planszy
	{
		if(field==Field.BLACKSTONE)
			field=Field.BLACKTERRITORY;
		else if(field==Field.WHITESTONE)
			field=Field.WHITETERRITORY;
		
		for(int i=0; i<chain.length; i++)
			for(int j=0; j<chain.length; j++)
			{
				if(chain[i][j]==true)
					boardReturn[i+1][j+1]=field;
			}
	}
	
	/**
	 * Method which checks if area is territory of any player
	 * @param i first attribute position
	 * @param j second attribute position
	 * @return Territory colour
	 */
	private Field checkIfAreaIsTerritory(int i, int j)
	{
		chain[i-1][j-1]=true;
		Field field1=checkIfAreaIsTerritoryRecursive(i+1, j);
		Field field2=checkIfAreaIsTerritoryRecursive(i, j+1);
		Field field3=checkIfAreaIsTerritoryRecursive(i-1, j);
		Field field4=checkIfAreaIsTerritoryRecursive(i, j-1);
		
		if(field1==Field.EMPTY && field2==Field.EMPTY && field3==Field.EMPTY &&  field4==Field.EMPTY)
			return Field.EMPTY;
		
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
	
	/**
	 * Recursive method using by checkIfAreaIsTerritory()
	 * @param i first attribute position
	 * @param j second attribute position
	 * @return Colour of current part of territory
	 */
	private Field checkIfAreaIsTerritoryRecursive(int i, int j)
	{
		switch(boardReturn[i][j])
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
	
	/**
	 * Checks if Chain is dead (nearly captive chain)
	 * @param i first attribute position
	 * @param j second attribute position
	 * @return 'Live Points' of chain
	 */
	private int checkIfChainIsDead(int i, int j) //zwraca ilośc całkowitych "punktów życia" łańcucha
	{
		Field field, concur;
		if(boardCopy[i][j]!=Field.BLACKSTONE && boardCopy[i][j]!=Field.WHITESTONE)
			return 100;
		field=boardCopy[i][j];
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
	
	/**
	 * Method which checks if chain have eyes
	 * @return number of eyes
	 */
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
	
	/**
	 * Recursive method uses by checkIfChainIsDead()
	 * @param i first attribute position
	 * @param j second attribute position
	 * @param field Colour of player
	 * @param concur Colour of concurrent
	 * @return 'life points' of current part of chain
	 */
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
	
	/**
	 * Recursive method uses by checkIfChainIsDeadRecursive()
	 * @param i first attribute position
	 * @param j second attribute position
	 * @param field Colour of player
	 * @param concur Colour of concurrent
	 * @return 'life points' of current part of chain
	 */
	private int checkIfChainIsDeadRecursiveTwo(int i, int j, Field field, Field concur) //Dopełnia pośrednią rekurencję dla checkIfChainIsDeadRecursive
	{
		switch(boardCopy[i][j])
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
	
	/**
	 * Checks briefs of current chain
	 * @param i first attribute position
	 * @param j second attribute position
	 * @param field Colour of player
	 * @param concur Colour of concurrent
	 * @return 'life points' of current brief
	 */
	private int checkEmptyField(int i, int j, Field field, Field concur) //Sprawdza ilośc "punktów życiowych" dla oddechu w łańcuchu
	{
		int lifePoints=0;
		if(boardCopy[i][j]==Field.EMPTY)
		{
			if(boardCopy[i+1][j]==Field.EMPTY)
				lifePoints++;
			if(boardCopy[i-1][j]==Field.EMPTY)
				lifePoints++;
			if(boardCopy[i][j+1]==Field.EMPTY)
				lifePoints++;
			if(boardCopy[i][j-1]==Field.EMPTY)
				lifePoints++;
		}
		else if(boardCopy[i][j]==field && !chain[i-1][j-1])
		{
			if(checkFriendChain(i, j, field, concur))
				lifePoints+=3;
		}
		return lifePoints;
	}

	/**
	 * Method which checks if friend chain is nearly dead
	 * @param i first attribute position
	 * @param j second attribute position
	 * @param field Colour of player
	 * @param concur Colour of concurrent
	 * @return True if chain isn't nearly dead
	 */
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
	
	/**
	 * Recursive methos uses by checkFriendChain()
	 * @param i first attribute position
	 * @param j second attribute position
	 * @param field Colour of player
	 * @param concur Colour of concurrent
	 * @throws TheSameChainException Is throwing if thw friend chain is the same current chain
	 */
	private void checkFriendChainRecursive(int i, int j, Field field, Field concur) throws TheSameChainException //Rekurencyjna metoda wykorzystywana przez checkFriendChain
	{
		friendChain[i-1][j-1]=true;

		if(i>1)
			if(chain[i-2][j-1]==true)
				throw new TheSameChainException();	
		if(j>1)
			if(chain[i-1][j-2]==true)
				throw new TheSameChainException();
		if(i<chain.length)
			if(chain[i][j-1]==true)
				throw new TheSameChainException();
		if(j<chain.length)
			if(chain[i-1][j]==true)
				throw new TheSameChainException();
			
			
		if(boardCopy[i+1][j]==field && !friendChain[i][j-1])
			checkFriendChainRecursive(i+1, j, field, concur);
		else if(boardCopy[i+1][j]==Field.EMPTY)
			emptyFieldsFriendChain++;
		
		if(boardCopy[i-1][j]==field && !friendChain[i-2][j-1])
			checkFriendChainRecursive(i-1, j, field, concur);
		else if(boardCopy[i-1][j]==Field.EMPTY)
			emptyFieldsFriendChain++;
		
		if(boardCopy[i][j+1]==field && !friendChain[i-1][j])
			checkFriendChainRecursive(i, j+1, field, concur);
		else if(boardCopy[i][j+1]==Field.EMPTY)
			emptyFieldsFriendChain++;
		
		if(boardCopy[i][j-1]==field && !friendChain[i-1][j-2])
			checkFriendChainRecursive(i, j-1, field, concur);
		else if(boardCopy[i][j-1]==Field.EMPTY)
			emptyFieldsFriendChain++;
	}

	/**
	 * Adds dead chain as a territory of concurrent chain
	 */
	private void addDeadChainToTerritories() //Dodaje prawie martwe łańcuchy do zwracanej planszy (board)
	{
		for(int i=0; i<chain.length; i++)
			for(int j=0; j<chain.length; j++)
				if(chain[i][j]==true)
				{
					if(boardCopy[i+1][j+1]==Field.BLACKSTONE)
						boardCopy[i+1][j+1]=Field.DEADBLACK;
					if(boardCopy[i+1][j+1]==Field.WHITESTONE)
						boardCopy[i+1][j+1]=Field.DEADWHITE;
				}
	}
	
}
