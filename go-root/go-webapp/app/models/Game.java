package models;

import java.util.Arrays;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import models.msgs.AcceptTerritory;
import models.msgs.GameEnded;
import models.msgs.Move;
import models.msgs.Pass;
import models.msgs.Quit;
import models.msgs.RefreshBoard;
import models.msgs.Resign;
import models.msgs.ResumeGame;
import models.msgs.SendBoard;
import play.Logger;
import play.libs.Json;
import pwrrgmp2017.go.game.BotGameController;
import pwrrgmp2017.go.game.GameController;
import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exception.GameStillInProgressException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.GameStates.GameStateEnum;
import pwrrgmp2017.go.game.Model.GameBoard.Field;

public class Game extends UntypedActor
{
	private Field[][] currentBoard;
	protected GameController controller;
	protected Field[][] territoryBoard;
	protected boolean acceptedPreviousTurn;
	protected boolean territoryPhase;
	protected ActorRef blackPlayer, whitePlayer, currentPlayer;
	private String lastMove;

	Game(ActorRef blackPlayer, ActorRef whitePlayer, GameController controller)
	{
		this.controller = controller;
		this.blackPlayer = blackPlayer;
		this.whitePlayer = whitePlayer;
		this.currentPlayer = blackPlayer;
		this.lastMove = "GameStarted";

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

		this.currentBoard = controller.getBoardCopy();
		this.territoryPhase = false;
		this.acceptedPreviousTurn = false;
	}

	protected void initializeGame(ActorRef player) throws GameStillInProgressException, BadFieldException
	{
		currentPlayer = player;
		if (player == blackPlayer)
			controller.initialiseGame(Field.BLACKSTONE);
		else
			controller.initialiseGame(Field.WHITESTONE);
	}

	protected ActorRef getOpponent(ActorRef player)
	{
		return player == blackPlayer ? whitePlayer : blackPlayer;
	}

	protected void resign() throws GameIsEndedException
	{
		controller.resign();
	}

	protected void pass(ActorRef player) throws GameBegginsException, GameIsEndedException, BadFieldException
	{
		if (player == blackPlayer)
			controller.pass(Field.BLACKSTONE);
		else if (player == whitePlayer)
			controller.pass(Field.WHITESTONE);
	}

	protected void changeTerritory(int x, int y)
	{
		switch (territoryBoard[x][y])
		{
		case BLACKSTONE:
			territoryBoard[x][y] = Field.DEADBLACK;
			break;
		case BLACKTERRITORY:
			territoryBoard[x][y] = Field.WHITETERRITORY;
			break;
		case DEADBLACK:
			territoryBoard[x][y] = Field.BLACKSTONE;
			break;
		case DEADWHITE:
			territoryBoard[x][y] = Field.WHITESTONE;
			break;
		case EMPTY:
			territoryBoard[x][y] = Field.BLACKTERRITORY;
			break;
		case NONETERRITORY:
			territoryBoard[x][y] = Field.BLACKTERRITORY;
			break;
		case WALL:
			territoryBoard[x][y] = Field.WALL;
			break;
		case WHITESTONE:
			territoryBoard[x][y] = Field.DEADWHITE;
			break;
		case WHITETERRITORY:
			territoryBoard[x][y] = Field.NONETERRITORY;
			break;
		default:
			break;
		}
	}

	protected void refreshTerritory()
	{
		this.territoryBoard = this.controller.getPossibleTerritory();
	}
	
	protected void addMovement(int x, int y, Field playerField) throws BadFieldException, GameBegginsException, GameIsEndedException
	{
		this.controller.addMovement(x, y, playerField);
	}

	@Override
	public void onReceive(Object message) throws Exception
	{
		if (message instanceof Move)
		{
			onMove((Move) message);
		}
		else if (message instanceof Pass)
		{
			onPass((Pass) message);
		}
		else if (message instanceof Resign)
		{
			onResign((Resign) message);
		}
		else if (message instanceof AcceptTerritory)
		{
			onAcceptTerritory((AcceptTerritory) message);
		}
		else if (message instanceof ResumeGame)
		{
			onResumeGame((ResumeGame) message);
		}
		else if (message instanceof RefreshBoard)
		{
			onRefreshBoard((RefreshBoard) message);
		}
		else if (message instanceof Quit)
		{
			onQuit((Quit) message);
		}
		else
		{
			unhandled(message);
		}

	}

	private void onQuit(Quit message)
	{
		lastMove = "Quit";
		getOpponent(getSender()).tell(new GameEnded("Your opponent has disconnected."), getSelf());
	}

	protected void onResumeGame(ResumeGame message)
	{
		lastMove = "ResumeGame";
		
		if (!territoryPhase || getSender() != currentPlayer)
		{
			return;
		}
		
		try
		{
			initializeGame(currentPlayer == blackPlayer ? whitePlayer : blackPlayer);
		}
		catch (GameStillInProgressException | BadFieldException e)
		{
			e.printStackTrace();
			return;
		}
		
		territoryPhase = false;
		getSelf().tell(new RefreshBoard(), getSender());
		getSelf().tell(new RefreshBoard(), getOpponent(getSender()));
	}

	private void onRefreshBoard(RefreshBoard message)
	{
		ObjectNode json = Json.newObject();
		this.currentBoard = controller.getBoardCopy();
		json.put("type", "updatedBoard");
		if (territoryPhase)
		{
			if (currentPlayer == blackPlayer)
			{
				json.put("state", "BLACKMOVETERRITORY");
			}
			else
			{
				json.put("state", "WHITEMOVETERRITORY");
			}
		}
		else
		{
			json.put("state", controller.getState().toString());
		}
		json.put("stats", generateStatistics());

		ArrayNode boardArray = json.arrayNode();
		boolean[][] possibleMovements = new boolean[currentBoard.length - 2][currentBoard.length - 2];
		try
		{
			if (controller.getState() == GameStateEnum.BLACKMOVE && getSender() == blackPlayer)
			{
				possibleMovements = controller.getPossibleMovements(Field.BLACKSTONE);
			}
			else if (controller.getState() == GameStateEnum.WHITEMOVE && getSender() == whitePlayer)
			{
				possibleMovements = controller.getPossibleMovements(Field.WHITESTONE);
			}
			else if (territoryPhase)
			{
				if (getSender() == currentPlayer)
				{
					for (int i = 0; i < possibleMovements.length; ++i)
						Arrays.fill(possibleMovements[i], true);
				}
			}
		}
		catch (BadFieldException e)
		{
			e.printStackTrace();

			return;
		}

		for (int i = 1; i < currentBoard.length - 1; ++i)
		{
			ArrayNode boardRow = json.arrayNode();
			for (int j = 1; j < currentBoard[i].length - 1; ++j)
			{
				ObjectNode field = Json.newObject();
				if (territoryPhase)
				{
					field.put("field", territoryBoard[i][j].toString());
				}
				else
				{
					field.put("field", currentBoard[i][j].toString());
				}
				field.put("possible", possibleMovements[i - 1][j - 1]);
				boardRow.add(field);
			}
			boardArray.add(boardRow);
		}
		json.put("board", boardArray);

		getSender().tell(new SendBoard(json), getSelf());
	}

	private String generateStatistics()
	{
		StringBuilder stats = new StringBuilder();

		// stats.append("Turn: 0\n"); // Not yet implemented
		if (territoryPhase)
		{
			if (currentPlayer == blackPlayer)
			{
				stats.append("BLACKMOVETERRITORY");
			}
			else
			{
				stats.append("WHITEMOVETERRITORY");
			}
		}
		else
		{
			stats.append(controller.getState().toString());
		}
		stats.append("<br />");
		stats.append("Last move: " + lastMove + "<br />");

		if (controller.getState() == GameStateEnum.END)
		{
			float score = controller.calculateScore(territoryBoard);
			String winner;
			if (score < 0)
			{
				winner = "white";
				score *= -1;
			}
			else if (score > 0)
			{
				winner = "black";
			}
			else
			{
				winner = "nobody";
			}
			stats.append("Score: " + winner + " wins by " + score + " points<br />");
		}

		stats.append("<br />");

		stats.append("Game settings<br />");
		stats.append("Game rules: Japanese<br />");
		stats.append("Board size: " + (currentBoard.length - 2) + "x" + (currentBoard.length - 2) + "<br />");
		stats.append("Komi: " + controller.getKomi() + "<br />");
		stats.append("Bot: " + (controller instanceof BotGameController ? "Yes" : "No") + "<br />");
		stats.append("<br />");

		stats.append("Black player captives: " + controller.getWhiteCaptives() + "<br />");
		stats.append("White player captives: " + controller.getBlackCaptives() + "<br />");

		return stats.toString();
	}
	
	private String getWinner()
	{
		float score;
		if (territoryPhase)
		{
			score = controller.calculateScore(territoryBoard);
		}
		else
		{
			score = controller.calculateScore();
		}
		String winner;
		if (score < 0)
		{
			winner = "White";
			score *= -1;
		}
		else if (score > 0)
		{
			winner = "Black";
		}
		else
		{
			winner = "Nobody";
		}
		return winner + " wins by " + score + " points";
	}

	protected void onAcceptTerritory(AcceptTerritory message)
	{
		if (!territoryPhase || getSender() != currentPlayer)
		{
			return;
		}
		
		lastMove = "AcceptTerritory";
		
		if (acceptedPreviousTurn)
		{
			getSender().tell(new GameEnded(getWinner()), getSelf());
			getOpponent(getSender()).tell(new GameEnded(getWinner()), getSelf());
			return;
		}
		
		acceptedPreviousTurn = true;
		currentPlayer = getOpponent(currentPlayer);
		getSelf().tell(new RefreshBoard(), getSender());
		getSelf().tell(new RefreshBoard(), getOpponent(getSender()));
	}

	protected void onResign(Resign message)
	{
		lastMove = "Resign";
		try
		{
			if (!territoryPhase)
			{
				resign();
			}
			
			getSender().tell(new GameEnded("You lose by resignation."), getSelf());
			getOpponent(getSender()).tell(new GameEnded("You win by resignation."), getSelf());
		}
		catch (GameIsEndedException e)
		{
			e.printStackTrace();
		}
	}

	protected void onPass(Pass message)
	{
		lastMove = "Pass";
		
		try
		{
			pass(currentPlayer);
			
			if (controller.getState() == GameStateEnum.END)
			{
				if (controller instanceof BotGameController)
				{
					getSender().tell(new GameEnded(getWinner()), getSelf());
					return;
				}
				territoryBoard = controller.getPossibleTerritory();
				territoryPhase = true;
				acceptedPreviousTurn = false;
			}
			
			if (! (controller instanceof BotGameController))
			{
				currentPlayer = getOpponent(currentPlayer);
			}
			
			getSelf().tell(new RefreshBoard(), getSender());
			getSelf().tell(new RefreshBoard(), getOpponent(getSender()));
		}
		catch (GameBegginsException | GameIsEndedException | BadFieldException e)
		{
			e.printStackTrace();
		}
	}

	private void onMove(Move message)
	{
		lastMove = "Move("+message.x+", "+message.y+")";
		GameStateEnum state = controller.getState();
		if (state == GameStateEnum.BLACKMOVE && getSender() == blackPlayer)
		{
			try
			{
				addMovement(message.x + 1, message.y + 1, Field.BLACKSTONE);
			}
			catch (BadFieldException | GameBegginsException | GameIsEndedException e)
			{
				e.printStackTrace();
			}
			
			if (!(controller instanceof BotGameController))
			{
				currentPlayer = getOpponent(currentPlayer);
			}
		}
		else if (state == GameStateEnum.WHITEMOVE && getSender() == whitePlayer)
		{
			try
			{
				addMovement(message.x + 1, message.y + 1, Field.WHITESTONE);
			}
			catch (BadFieldException | GameBegginsException | GameIsEndedException e)
			{
				e.printStackTrace();
			}
			
			if (!(controller instanceof BotGameController))
			{
				currentPlayer = getOpponent(currentPlayer);
			}
		}
		else if (territoryPhase)
		{
			if (getSender() == currentPlayer)
			{
				lastMove += " (territory)";
				changeTerritory(message.x+1, message.y+1);
				acceptedPreviousTurn = false;
			}
		}
		else
		{
			Logger.warn("Wrong move.");
			return;
		}
		
		getSelf().tell(new RefreshBoard(), getSender());
		getSelf().tell(new RefreshBoard(), getOpponent(getSender()));
	}

}
