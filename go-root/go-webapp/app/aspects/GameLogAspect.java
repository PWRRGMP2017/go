package aspects;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

import akka.actor.ActorRef;
import models.Game;
import models.Player;
import models.msgs.AcceptTerritory;
import models.msgs.GameEnded;
import models.msgs.Move;
import models.msgs.Pass;
import models.msgs.Resign;
import models.msgs.ResumeGame;
import play.Logger;
import pwrrgmp2017.go.game.GameController;

@Aspect
public class GameLogAspect
{
	private final Map<ActorRef, GameLog> gameLogs =  new HashMap<>();
	private final String LOG_FILES_PATH = "./public/gamelogs/";
	
	private PrintWriter createLogFileWriter(String name)
	{
		File logFile = new File(LOG_FILES_PATH + name + ".txt");
		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter(new FileWriter(logFile), true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return writer;
	}
	
	@AfterReturning("execution(models.Game.new(..))")
	public void createNewLog(JoinPoint joinPoint)
	{
		Game game = (Game) joinPoint.getThis();
		ActorRef gameActor = game.getSelf();
		Object[] args = joinPoint.getArgs();
		ActorRef blackPlayer = (ActorRef) args[0];
		ActorRef whitePlayer = (ActorRef) args[1];
		GameController gameController = (GameController) args[2];
		String blackPlayerName = Player.tryGetName(blackPlayer);
		String whitePlayerName = Player.tryGetName(whitePlayer);
		PrintWriter writer = createLogFileWriter(blackPlayerName + "_vs_" + whitePlayerName);
		if (writer == null)
		{
			Logger.warn("Could not create a writer for log file.");
			return;
		}
		GameLog log = new GameLog(writer, gameActor, blackPlayer, whitePlayer, blackPlayerName, whitePlayerName, gameController);
		gameLogs.put(gameActor, log);
	}
	
	@AfterReturning("execution(* models.Game.addMovement(..))")
	public void logMove(JoinPoint joinPoint)
	{
		int x = (int) joinPoint.getArgs()[0];
		int y = (int) joinPoint.getArgs()[0];
		GameLog log = gameLogs.get(joinPoint.getThis());
		if (log == null)
		{
			return;
		}
		log.writeMove(x, y);
	}
	
	@AfterReturning("execution(* models.Game.changeTerritory(..))")
	public void logChangeTerritory(JoinPoint joinPoint)
	{
		int x = (int) joinPoint.getArgs()[0];
		int y = (int) joinPoint.getArgs()[0];
		GameLog log = gameLogs.get(joinPoint.getThis());
		if (log == null)
		{
			return;
		}
		log.writeChangeTerritory(x, y);
	}
	
	@After("execution(* models.Game.onReceive(..))")
	public void logGameState(JoinPoint joinPoint)
	{
		Game game = (Game)joinPoint.getThis();
		ActorRef gameActor = game.getSelf();
		GameLog log = gameLogs.get(gameActor);
		if (log == null)
		{
			return;
		}
		
		Object message = joinPoint.getArgs()[0];
		if (message instanceof Move)
		{
		}
		else if (message instanceof Pass)
		{
			log.writePass();
		}
		else if (message instanceof Resign)
		{
//			log.writeResign(loser);
		}
		else if (message instanceof AcceptTerritory)
		{
			log.writeAcceptTerritory();
		}
		else if (message instanceof ResumeGame)
		{
			log.writeResume();
		}
	}
	
	@After("execution(* models.Player.onReceive(..))")
	public void logGameEnded(JoinPoint joinPoint)
	{
		Object message = joinPoint.getArgs()[0];
		if (!(message instanceof GameEnded))
		{
			return;
		}
		
		GameEnded gameEnded = (GameEnded) message;
		Player player = (Player) joinPoint.getThis();
		ActorRef game = player.getSender();
		GameLog log = gameLogs.remove(game);
		if (log == null)
		{
			return;
		}
		
		log.writeEndGame(player.getSelf(), gameEnded.stats);
	}
}
