package pwrrgmp2017.go.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import pwrrgmp2017.go.server.Exceptions.LostPlayerConnection;

public class GamesManager
{
	private List<Game> games;
	private ConcurrentHashMap<String, PlayerConnection> choosingPlayers;
	private List<PlayerConnection> playingPlayers;
	private LinkedList<PlayerConnection> waitingPlayers;

	GamesManager()
	{
		games = new ArrayList<Game>();
		choosingPlayers = new ConcurrentHashMap<String, PlayerConnection>();
		playingPlayers = new ArrayList<PlayerConnection>();
		// waitingPlayers = (LinkedHashMap<String, PlayerConnection>)
		// Collections.synchronizedMap(new
		// LinkedHashMap<String,PlayerConnection>());
		waitingPlayers = new LinkedList<PlayerConnection>();
	}

	public void closeAllConnections()
	{
		for (PlayerConnection connection : playingPlayers)
		{
			connection.close();
		}

		for (PlayerConnection connection : choosingPlayers.values())
		{
			connection.close();
		}

		for (PlayerConnection connection : waitingPlayers)
		{
			connection.close();
		}

		for (Game game : games)
		{
			// TODO
		}

		playingPlayers.clear();
		choosingPlayers.clear();
		waitingPlayers.clear();
		games.clear();
	}

	public void createPlayerConnection(Socket socket) throws IOException
	{
		waitingPlayers.add(new RealPlayerConnection(socket));
	}

	public void createGame(PlayerConnection player, PlayerConnection opponent, Object typeGame) // enum?
	{
		// opponent także jako singleton bot
	}

	public void deletePlayer(PlayerConnection player) throws LostPlayerConnection
	{

	}
	// Nie przemyślane do końca, trzeba wymyślec zasady funkcjonowania
	// wybierania i tworzenia gier, wychodzenia graczy lub zakończenia gier,
	// łączenia graczy ze sobą, oczekiwania na przygotowanie graczy do gry,
	// itp....
}
