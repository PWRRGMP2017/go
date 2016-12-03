package pwrrgmp2017.go.server;

import java.util.ArrayList;
import java.util.List;

public class GamesManager
{
	List<Game> games;
	List<PlayerConnection> chosingPlayers;
	List<PlayerConnection> playingPlayers;
	List<PlayerConnection> waitingPlayers;

	GamesManager()
	{
		games= new ArrayList<Game>();
		chosingPlayers= new ArrayList<PlayerConnection>();
		playingPlayers= new ArrayList<PlayerConnection>();
		waitingPlayers= new ArrayList<PlayerConnection>();
	}
	// Nie przemyślane do końca, trzeba wymyślec zasady funkcjonowania wybierania i tworzenia gier, wychodzenia graczy lub zakończenia gier,
	// łączenia graczy ze sobą, oczekiwania na przygotowanie graczy do gry, itp....
}
