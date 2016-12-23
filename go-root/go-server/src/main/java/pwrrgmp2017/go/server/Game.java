package pwrrgmp2017.go.server;

import java.awt.Point;
import java.io.IOException;
import java.util.logging.Logger;

import pwrrgmp2017.go.clientserverprotocol.AcceptTerritoryProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ChangeTerritoryProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ExitProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.MoveProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.PassProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ResignProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ResumeGameProtocolMessage;
import pwrrgmp2017.go.game.BotGameController;
import pwrrgmp2017.go.game.GameController;
import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exception.GameStillInProgressException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.GameStates.GameStateEnum;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.server.Exceptions.BadPlayerException;
import pwrrgmp2017.go.server.connection.PlayerConnection;

/**
 * Thread for the game currently played by two players.
 */
public class Game extends Thread
{
	/**
	 * Reference to logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(Game.class.getName());

	/**
	 * Controller of the game playing.
	 */
	private GameController controller;

	/**
	 * Reference to black player connection.
	 */
	private PlayerConnection blackPlayer;

	/**
	 * Reference to white player connection.
	 */
	private PlayerConnection whitePlayer;

	/**
	 * Reference to the games manager.
	 */
	private GamesManager gamesManager;

	/**
	 * Reference the player whom turn it is.
	 */
	private PlayerConnection currentPlayer;

	/**
	 * Flag saying if the previous turn was acceptance of the territory.
	 */
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
	Game(PlayerConnection blackPlayer, PlayerConnection whitePlayer, GameController controller,
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Listens for messages from the players and reacts to them.
	 */
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
				try
				{
					resign();
				}
				catch (GameIsEndedException e)
				{
					// So what?
					// e.printStackTrace();
				}
				ResignProtocolMessage resignation = (ResignProtocolMessage) genericMessage;
				getOpponent(currentPlayer).send(resignation.getFullMessage());
				gamesManager.deleteGame(this);
				return;
			}
			else if (genericMessage instanceof MoveProtocolMessage)
			{
				LOGGER.info("Player " + currentPlayer.getPlayerName() + " made a move.");

				// Let's make a move
				MoveProtocolMessage movement = (MoveProtocolMessage) genericMessage;
				try
				{
					addTurnMessage(currentPlayer, movement.getX(), movement.getY());
					if (controller instanceof BotGameController)
					{
						Point botMovement = controller.getLastMovement();
						// LOGGER.info(Integer.toString(movement.getX()) + ", "
						// + Integer.toString(movement.getY()));
						// LOGGER.info(Integer.toString((int)botMovement.getX())
						// + ", " + Integer.toString((int)botMovement.getY()));
						// LOGGER.info(Boolean.toString((int)botMovement.getX()
						// == movement.getX() && (int)botMovement.getY() ==
						// movement.getY()));
						if ((int) botMovement.getX() == movement.getX() && (int) botMovement.getY() == movement.getY())
						{
							// Bot passed
							currentPlayer.send(new PassProtocolMessage().getFullMessage());
						}
						else
						{
							currentPlayer
									.send(new MoveProtocolMessage((int) botMovement.getX(), (int) botMovement.getY())
											.getFullMessage());
						}
					}
				}
				catch (BadPlayerException | GameIsEndedException | GameBegginsException e)
				{
					e.printStackTrace();
					continue;
				}
				catch (BadFieldException e)
				{
					LOGGER.info("Player " + currentPlayer.getPlayerName() + " made move for the opponent.");
					e.printStackTrace();
					continue;
				}

				// Send the movement to the opponent
				getOpponent(currentPlayer).send(movement.getFullMessage());

				// Now it's their turn
				currentPlayer = getOpponent(currentPlayer);
			}
			else if (genericMessage instanceof PassProtocolMessage)
			{
				LOGGER.info("Player " + currentPlayer.getPlayerName() + " passed.");

				// Pass in here
				try
				{
					pass(currentPlayer);
					if (controller instanceof BotGameController)
					{
						currentPlayer.send(new PassProtocolMessage().getFullMessage());
					}
				}
				catch (GameBegginsException | GameIsEndedException | BadFieldException e)
				{
					// Should not happen
					e.printStackTrace();
				}

				// Send the movement to the opponent
				currentPlayer = getOpponent(currentPlayer);
				currentPlayer.send(genericMessage.getFullMessage());
			}
			else if (genericMessage instanceof ChangeTerritoryProtocolMessage)
			{
				LOGGER.info("Player " + currentPlayer.getPlayerName() + " changes territory.");
				acceptedPreviousTurn = false;

				getOpponent(currentPlayer).send(genericMessage.getFullMessage());
			}
			else if (genericMessage instanceof AcceptTerritoryProtocolMessage)
			{
				LOGGER.info("Player " + currentPlayer.getPlayerName() + " accepts territory.");

				currentPlayer = getOpponent(currentPlayer);
				currentPlayer.send(genericMessage.getFullMessage());

				if (acceptedPreviousTurn)
				{
					LOGGER.info("Players " + blackPlayer.getPlayerName() + " and " + whitePlayer.getPlayerName()
							+ " have ended the game.");
					gamesManager.deleteGame(this);
					return;
				}

				acceptedPreviousTurn = true;
			}
			else if (genericMessage instanceof ResumeGameProtocolMessage)
			{
				LOGGER.info("Player " + currentPlayer.getPlayerName() + " wants to resume the game.");
				acceptedPreviousTurn = false;

				currentPlayer = getOpponent(currentPlayer);

				try
				{
					initializeGame(currentPlayer);
				}
				catch (GameStillInProgressException e)
				{
					LOGGER.warning(
							"Player " + currentPlayer.getPlayerName() + " wanted to resume a game still in progress.");
					continue;
				}
				catch (BadFieldException e)
				{
					// Should not happen
					e.printStackTrace();
				}

				currentPlayer.send(new ResumeGameProtocolMessage().getFullMessage());
			}
			else if (message.isEmpty() && controller instanceof BotGameController)
			{
				// The bot turn
				if (controller.getState() == GameStateEnum.END)
				{
					// The game ended
					LOGGER.info("Players " + blackPlayer.getPlayerName() + " and bot have ended the game.");
					gamesManager.deleteGame(this);
					return;
				}

				currentPlayer = getOpponent(currentPlayer);
			}
			else
			{
				LOGGER.warning("Got wrong message from client: " + genericMessage.getFullMessage());
			}
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
		gamesManager.deleteGame(this);
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

}
