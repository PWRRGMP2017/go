package pwrrgmp2017.go.server;

import java.util.logging.Logger;

import pwrrgmp2017.go.clientserverprotocol.ResignProtocolMessage;
import pwrrgmp2017.go.game.GameController;
import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exception.GameStillInProgressException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.server.Exceptions.BadPlayerException;
import pwrrgmp2017.go.server.connection.PlayerConnection;

/**
 * Thread for the game currently played by two players.
 */
public class Game2 extends Thread
{
	/** Reference to logger.*/
	private static final Logger LOGGER = Logger.getLogger(Game.class.getName());

	/** Controller of the game playing.*/
	private GameController controller;

	/** Reference to black player connection.*/
	private PlayerConnection blackPlayer;

	/** Reference to white player connection.*/
	private PlayerConnection whitePlayer;

	/** Reference to the games manager.*/
	private GamesManager gamesManager;

	/** Reference the player whom turn it is.*/
	private PlayerConnection currentPlayer;
	
	private Field[][] territoryBoard;

	/** Flag saying if the previous turn was acceptance of the territory.*/
	private boolean acceptedPreviousTurn;

	/**
	 * Initialises the game, but does not start a thread.
	 * 
	 * @param blackPlayer
	 *            black player connection
	 * @param whitePlayer
	 *            white player connection
	 * @param controller
	 *            game controller
	 * @param gamesManager
	 *            reference to the games manager
	 */
	Game2(PlayerConnection blackPlayer, PlayerConnection whitePlayer, GameController controller,
			GamesManager gamesManager)
	{
		this.controller = controller;
		this.blackPlayer = blackPlayer;
		this.whitePlayer = whitePlayer;
		this.gamesManager = gamesManager;
		this.currentPlayer = blackPlayer;

		try
		{
			controller.initialiseGame(Field.BLACKSTONE);
		}
		catch (GameStillInProgressException e)
		{
			// Should not happen
			e.printStackTrace();
		}
		catch (BadFieldException e)
		{
			e.printStackTrace();
		}
	}

	/** Listens for messages from the players and reacts to them.
	 */
	@Override
	public void run()
	{
		String message;
		while (!Thread.currentThread().isInterrupted())
		{
			
		}

		cleanUp();
	}

	/**
	 * On error, closes the connection with {@link #currentPlayer}, sends a
	 * message to the opponent and requests {@link #gamesManager} to delete this
	 * game.
	 */
	private void cleanUp()
	{
		currentPlayer.close();

		// Send the other player a sad message ;)
		ResignProtocolMessage message = new ResignProtocolMessage("Your opponent closed the connection.");
		getOpponent(currentPlayer).send(message.getFullMessage());

		// Let the games manager clean up
		//gamesManager.deleteGame(this);
	}
	
	/**
	 * @return black player connection
	 */
	public PlayerConnection getBlackPlayer()
	{
		return blackPlayer;
	}
	
	/**
	 * @return white player connection
	 */
	public PlayerConnection getWhitePlayer()
	{
		return whitePlayer;
	}
	
	/**
	 * Reinitialises the game during the territory phase.
	 * @param player player which should start the next turn
	 * @throws GameStillInProgressException if the game is still in progress
	 * @throws BadFieldException should not be thrown
	 */
	private void initializeGame(PlayerConnection player) throws GameStillInProgressException, BadFieldException
	{
		if (player == blackPlayer)
			controller.initialiseGame(Field.BLACKSTONE);
		else
			controller.initialiseGame(Field.WHITESTONE);
	}
	
	/**
	 * Helping method.
	 * @param player player
	 * @return opponent to the player
	 */
	private PlayerConnection getOpponent(PlayerConnection player)
	{
		return player == blackPlayer ? whitePlayer : blackPlayer;
	}
	
	/**
	 * Makes a move.
	 * @param player player who made a move
	 * @param x position on the board
	 * @param y position on the board
	 * @throws BadPlayerException if wrong player made a move
	 * @throws BadFieldException if wrong player made a move
	 * @throws GameBegginsException if game is was not initialised
	 * @throws GameIsEndedException if game is in end state
	 */
	private void addTurnMessage(PlayerConnection player, int x, int y)
			throws BadPlayerException, BadFieldException, GameBegginsException, GameIsEndedException
	{
		if (player == blackPlayer)
			controller.addMovement(x, y, Field.BLACKSTONE);
		else if (player == whitePlayer)
			controller.addMovement(x, y, Field.WHITESTONE);

	}
	
	/**
	 * The current player resigns.
	 * @throws GameIsEndedException if the game is in the end state
	 */
	private void resign() throws GameIsEndedException
	{
		controller.resign();
	}
	
	/**
	 * The player passes.
	 * @param player player which passes
	 * @throws GameBegginsException if the game was not initialised
	 * @throws GameIsEndedException if the game is in end state
	 * @throws BadFieldException should not be thrown
	 */
	private void pass(PlayerConnection player) throws GameBegginsException, GameIsEndedException, BadFieldException
	{
		if (player == blackPlayer)
			controller.pass(Field.BLACKSTONE);
		else if (player == whitePlayer)
			controller.pass(Field.WHITESTONE);
	}
	
	
	private void changeTerritory(int x, int y)
	{
		switch (territoryBoard[x][y])
		{
		case BLACKSTONE:
			territoryBoard[x][y]=Field.DEADBLACK;
			break;
		case BLACKTERRITORY:
			territoryBoard[x][y]=Field.WHITETERRITORY;
			break;
		case DEADBLACK:
			territoryBoard[x][y]=Field.BLACKSTONE;
			break;
		case DEADWHITE:
			territoryBoard[x][y]=Field.WHITESTONE;
			break;
		case EMPTY:
			territoryBoard[x][y]=Field.BLACKTERRITORY;
			break;
		case NONETERRITORY:
			territoryBoard[x][y]=Field.BLACKTERRITORY;
			break;
		case WALL:
			territoryBoard[x][y]=Field.WALL;
			break;
		case WHITESTONE:
			territoryBoard[x][y]=Field.DEADWHITE;
			break;
		case WHITETERRITORY:
			territoryBoard[x][y]=Field.NONETERRITORY;
			break;
		default:
			break;
		}
	}
	
	private void refreshTerritory()
	{
		this.territoryBoard=this.controller.getPossibleTerritory();
	}
	

}