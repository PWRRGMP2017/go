package pwrrgmp2017.go.game.Model;

import pwrrgmp2017.go.game.Model.GameBoard.Field;

public class JapanGameModel extends GameModel
{
	Field[][] board;

	public JapanGameModel(GameBoard board, float komi)
	{
		super(board, komi);
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
		Field[][] board=super.getBoardCopy();
		for(int i=1; i<board.length; i++)
			for(int j=1; j<board.length; j++)
			{
				if(checkIfChainIsDead(i, j))	
					addDeadChainToTerritories();
			}
				
		
		return null;
	}

	private boolean checkIfChainIsDead(int i, int j)
	{
		Field field, concur;
		if(board[i][j]!=Field.BLACKSTONE && board[i][j]!=Field.WHITESTONE)
			return false;
		field=board[i][j];
		
		if(field==Field.BLACKSTONE)
			concur=Field.WHITESTONE;
		else
			concur=Field.BLACKSTONE;
			
		return checkIfChainIsDeadRecursive(i, j, field, concur);
	}
	
	private boolean checkIfChainIsDeadRecursive(int i, int j, Field field, Field concur)
	{
		board[i][j]=Field.WALL;
		
		if(!checkIfChainIsDeadRecursiveTwo(i-1, j, field, concur))
			return false;
		if(!checkIfChainIsDeadRecursiveTwo(i+1, j, field, concur))
			return false;
		if(!checkIfChainIsDeadRecursiveTwo(i, j+1, field, concur))
			return false;
		if(!checkIfChainIsDeadRecursiveTwo(i, j-1, field, concur))
			return false;
		
		board[i][j]=field;
		return false;
	}
	
	private boolean checkIfChainIsDeadRecursiveTwo(int i, int j, Field field, Field concur)
	{
		switch(board[i][j])
		{
		case BLACKSTONE:
			
			break;
		case WHITESTONE:
			
			break;
		case EMPTY:
			//sprawdzenie oddechów
		case WALL:
			//TODO
			break;
		default:
			//TODO
		}
		
		return false;
	}

	private void addDeadChainToTerritories()
	{
		// TODO Auto-generated method stub
		
	}
	
}
