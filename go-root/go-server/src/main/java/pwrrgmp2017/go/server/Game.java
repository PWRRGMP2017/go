package pwrrgmp2017.go.server;

import pwrrgmp2017.go.game.GameController;
import pwrrgmp2017.go.server.connection.PlayerConnection;

public class Game extends Thread
{
	private GameController controller;
	private PlayerConnection blackPlayer;
	private PlayerConnection whitePlayer;

	Game(PlayerConnection blackPlayer, PlayerConnection whitePlayer, GameController controller)
	{
		this.controller = controller;
		this.blackPlayer = blackPlayer;
		this.whitePlayer = whitePlayer;
	}

	public GameController getController()
	{
		return controller;
	}

	public PlayerConnection getBlackPlayer()
	{
		return blackPlayer;
	}

	public PlayerConnection getWhitePlayer()
	{
		return whitePlayer;
	}

	public PlayerConnection getOpponent(PlayerConnection player)
	{
		return player == blackPlayer ? whitePlayer : blackPlayer;
	}
}
