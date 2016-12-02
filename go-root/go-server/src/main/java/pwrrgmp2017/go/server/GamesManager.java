package pwrrgmp2017.go.server;

import java.util.ArrayList;
import java.util.List;

public class GamesManager
{
	List<Game> games;
	List<PlayerConnection> players;

	GamesManager()
	{
		games= new ArrayList<Game>();
		players= new ArrayList<PlayerConnection>();
	}
}
