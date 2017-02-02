package aspects;

import java.awt.Point;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import akka.actor.ActorRef;
import pwrrgmp2017.go.game.BotGameController;
import pwrrgmp2017.go.game.GameController;

public class GameLog
{
	public final ActorRef game;
	public final ActorRef blackPlayer;
	public final ActorRef whitePlayer;
	public final String blackPlayerName;
	public final String whitePlayerName;
	public final PrintWriter writer;
	public final GameController gameController;
	public final boolean isBot;

	private String currentPlayer;
	private final static String BLACK_LOG = "[Black] ";
	private final static String WHITE_LOG = "[White] ";

	private LocalDateTime start;
	private LocalDateTime end;
	private LocalDateTime lastMove;
	private List<Duration> blackIdleTimes;
	private List<Duration> whiteIdleTimes;
	private List<Duration> currentIdleTimes;

	public GameLog(final PrintWriter writer, final ActorRef game, final ActorRef blackPlayer, final ActorRef whitePlayer,
			final String blackPlayerName, final String whitePlayerName, final GameController gameController)
	{
		this.writer = writer;
		this.game = game;
		this.blackPlayer = blackPlayer;
		this.whitePlayer = whitePlayer;
		this.blackPlayerName = blackPlayerName;
		this.whitePlayerName = whitePlayerName;
		this.gameController = gameController;

		currentPlayer = "[Black] ";

		start = LocalDateTime.now();
		blackIdleTimes = new LinkedList<>();
		whiteIdleTimes = new LinkedList<>();
		lastMove = start;
		end = null;
		currentIdleTimes = blackIdleTimes;

		writer.println("Game Started " + start.toString().replaceAll("T", " "));
		writer.println("Black Player Name: " + blackPlayerName);
		writer.println("White Player Name: " + whitePlayerName);

		writer.println("Komi: " + gameController.getKomi());
		writer.println("Board Size: " + (gameController.getBoardCopy().length - 2));
		this.isBot = (gameController instanceof BotGameController);
		writer.println("Bot: " + isBot);
		writer.println();
	}

	private void calculateDuration()
	{
		LocalDateTime newMove = LocalDateTime.now();
		currentIdleTimes.add(Duration.between(lastMove, newMove));
		lastMove = newMove;
		if (currentPlayer == BLACK_LOG)
		{
			currentIdleTimes = whiteIdleTimes;
		}
		else
		{
			currentIdleTimes = blackIdleTimes;
		}
	}

	public void writeMove(int x, int y)
	{
		calculateDuration();
		writer.println(currentPlayer + "Move(" + x + ", " + y + ")");
		if (isBot)
		{
			calculateDuration();
			Point botMove = gameController.getLastMovement();
			writer.println((currentPlayer == BLACK_LOG ? WHITE_LOG : BLACK_LOG) + "Move(" + (int) botMove.getX() + ", "
					+ (int) botMove.getY() + ")");
		}
		swapCurrentPlayer();
	}

	public void writeChangeTerritory(int x, int y)
	{
		calculateDuration();
		writer.println(currentPlayer + "ChangeTerritory(" + x + ", " + y + ")");
	}

	public void writePass()
	{
		calculateDuration();
		writer.println(currentPlayer + "Pass");
		if (isBot)
		{
			calculateDuration();
			writer.println((currentPlayer == BLACK_LOG ? WHITE_LOG : BLACK_LOG) + "Pass");
		}
		swapCurrentPlayer();
	}

	public void writeResign(ActorRef loser)
	{
		calculateDuration();
		String player;
		if (loser == blackPlayer)
		{
			player = BLACK_LOG;
		}
		else
		{
			player = WHITE_LOG;
		}
		writer.println(player + "Resign");
	}

	public void writeAcceptTerritory()
	{
		calculateDuration();
		writer.println(currentPlayer + "Accept Territory");
		swapCurrentPlayer();
	}

	public void writeResume()
	{
		calculateDuration();
		writer.println(currentPlayer + "Resume Game");
		swapCurrentPlayer();
	}

	public void writeEndGame(ActorRef player, String winMessage)
	{
		end = LocalDateTime.now();
		writer.println("Game Ended " + end.toString().replaceAll("T", " "));

		// Statistics
		if (winMessage.toLowerCase().contains("you"))
		{
			String receiver = player == blackPlayer ? "[Black] " : "[White] ";
			winMessage = receiver + winMessage;
		}
		writer.println(winMessage);
		writer.println("Average Black Turn Time: " + formatDuration(calculateAverage(blackIdleTimes)));
		writer.println("Average White Turn Time: " + formatDuration(calculateAverage(whiteIdleTimes)));
		writer.close();
	}

	private Duration calculateAverage(List<Duration> list)
	{
		Duration total = Duration.ZERO;
		int size = list.size();
		if (size == 0)
		{
			return total;
		}
		for (int i = 0; i < size; ++i)
		{
			total = total.plus(list.get(i));
		}
		return total.dividedBy(size);
	}

	private String formatDuration(Duration duration)
	{
		long days = duration.toDays();
		duration = duration.minusDays(days);
		long hours = duration.toHours();
		duration = duration.minusHours(hours);
		long minutes = duration.toMinutes();
		duration = duration.minusMinutes(minutes);
		long seconds = duration.getSeconds();
		duration = duration.minusSeconds(seconds);
		long miliseconds = duration.toMillis();
		return String.format("%d days %d hours %d minutes %d.%d seconds", days, hours, minutes, seconds, miliseconds);
	}

	private void swapCurrentPlayer()
	{
		if (isBot)
		{
			return;
		}

		if (currentPlayer == BLACK_LOG)
		{
			currentPlayer = WHITE_LOG;
		}
		else
		{
			currentPlayer = BLACK_LOG;
		}
	}
}
