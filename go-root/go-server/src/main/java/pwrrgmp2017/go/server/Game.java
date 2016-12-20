package pwrrgmp2017.go.server;

import java.awt.Point;
import java.io.IOException;
import java.util.logging.Logger;

import pwrrgmp2017.go.clientserverprotocol.ExitProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ResignProtocolMessage;
import pwrrgmp2017.go.game.GameController;
import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.server.Exceptions.BadPlayerException;
import pwrrgmp2017.go.server.connection.PlayerConnection;

public class Game extends Thread
{
	private static final Logger LOGGER = Logger.getLogger(Game.class.getName());

	private GameController controller;
	private PlayerConnection blackPlayer;
	private PlayerConnection whitePlayer;
	private GamesManager gamesManager;

	private PlayerConnection currentPlayer;

	Game(PlayerConnection blackPlayer, PlayerConnection whitePlayer, GameController controller,
			GamesManager gamesManager)
	{
		this.controller = controller;
		this.blackPlayer = blackPlayer;
		this.whitePlayer = whitePlayer;
		this.gamesManager = gamesManager;

		this.currentPlayer = blackPlayer;
	}

	@Override
	public void run()
	{
		String message;
		while (!Thread.currentThread().isInterrupted())
		{
			// Receive the message
			try
			{
				message = currentPlayer.receive();
			}
			catch (IOException e)
			{
				LOGGER.warning("Could not receive message from client " + currentPlayer.getPlayerName() + ": "
						+ e.getMessage());
				cleanUp();
				return;
			}

			if (message == null)
			{
				LOGGER.warning("Client " + currentPlayer.getPlayerName() + " unexpectedly closed the connection.");
				cleanUp();
				return;
			}

			// Interpret the message
			ProtocolMessage genericMessage = ProtocolMessage.getProtocolMessage(message);

			if (genericMessage instanceof ExitProtocolMessage)
			{
				LOGGER.info("Player " + currentPlayer.getPlayerName() + " wants to exit.");
				cleanUp();
				return;
			}
			else if (genericMessage instanceof ResignProtocolMessage)
			{
				LOGGER.info("Player " + currentPlayer.getPlayerName() + " resigned.");
				ResignProtocolMessage resignation = (ResignProtocolMessage) genericMessage;
				getOpponent(currentPlayer).send(resignation.getFullMessage());
				gamesManager.deleteGame(this);
				return;
			}
			// move, pass, resign etc.
			else
			{
				LOGGER.warning("Got wrong message from client: " + genericMessage.getFullMessage());
			}
		}

		cleanUp();
	}

	private void cleanUp()
	{
		currentPlayer.close();

		// Send the other player a sad message ;)
		ResignProtocolMessage message = new ResignProtocolMessage("Your opponent closed the connection.");
		getOpponent(currentPlayer).send(message.getFullMessage());

		// Let the games manager clean up
		gamesManager.deleteGame(this);
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

	public void addTurnMessage(PlayerConnection player, int x, int y)
			throws BadPlayerException, BadFieldException, GameBegginsException, GameIsEndedException
	{
		if (player == blackPlayer)
			controller.addMovement(x, y, Field.BLACKSTONE);
		else if (player == whitePlayer)
			controller.addMovement(x, y, Field.WHITESTONE);

	}

	public void resign() throws GameIsEndedException
	{
		controller.resign();
	}

	public void pass(PlayerConnection player) throws GameBegginsException, GameIsEndedException, BadFieldException
	{
		if (player == blackPlayer)
			controller.pass(Field.BLACKSTONE);
		else if (player == whitePlayer)
			controller.pass(Field.WHITESTONE);
	}

	public float calculateScore()
	{
		return controller.calculateScore();
	}

	public void calculateScore(Field[][] territory)
	{
		controller.calculateScore(territory);
	}

	public Field[][] getBoardCopy()
	{
		return controller.getBoardCopy();
	}

	public Point getLastMovement()
	{
		return controller.getLastMovement();
	}

	public boolean[][] getPossibleMovements(Field colour) throws BadFieldException
	{
		return controller.getPossibleMovements(colour);
	}

	public Field[][] getPossibleTerritory()
	{
		return controller.getPossibleTerritory();
	}

}
