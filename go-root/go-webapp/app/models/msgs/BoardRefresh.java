package models.msgs;

import pwrrgmp2017.go.game.Model.GameBoard.Field;

public class BoardRefresh
{
	Field[][] territories;
	
	public BoardRefresh(Field[][] territories)
	{
		this.territories=territories;
	}
}
