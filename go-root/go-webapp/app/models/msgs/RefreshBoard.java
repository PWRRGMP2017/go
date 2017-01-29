package models.msgs;

import pwrrgmp2017.go.game.Model.GameBoard.Field;

public class RefreshBoard
{
	Field[][] territories;
	
	public RefreshBoard(Field[][] territories)
	{
		this.territories=territories;
	}
}
