package models;

import static akka.pattern.Patterns.ask;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import models.msgs.CancelWaiting;
import models.msgs.CreateGame;
import models.msgs.GetPlayer;
import models.msgs.Join;
import models.msgs.PlayerAccepted;
import models.msgs.PlayerRejected;
import models.msgs.Quit;
import models.msgs.ReturnPlayer;
import models.msgs.WaitForGame;
import play.libs.Akka;
import play.mvc.WebSocket;
import pwrrgmp2017.go.game.GameController;
import pwrrgmp2017.go.game.factory.GameFactory;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

/**
 * Manages list of players on the server.
 */
public class PlayerRoom extends UntypedActor
{
	private static final ActorRef playerRoom = Akka.system().actorOf(Props.create(PlayerRoom.class));
	private static final Map<String, ActorRef> players = new HashMap<>();
	private static final Map<String, ActorRef> waitingplayers = new HashMap<>();

	public static boolean tryJoin(final String playerName, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out)
	{
		Object result;
		try
		{
			result = Await.result(ask(playerRoom, new Join(playerName, in, out), 1000),
					Duration.create(1, TimeUnit.SECONDS));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}

		if (result instanceof PlayerAccepted)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static ActorRef tryGetPlayer(final String playerName)
	{
		Object result;
		try
		{
			result = Await.result(ask(playerRoom, new GetPlayer(playerName), 1000),
					Duration.create(1, TimeUnit.SECONDS));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}

		if (result instanceof ReturnPlayer)
		{
			return ((ReturnPlayer) result).player;
		}
		else
		{
			return null;
		}
	}
	
	
	public static boolean cancelWaiting(final CancelWaiting message)
	{
		Object result;
		try
		{
			result = Await.result(ask(playerRoom, message, 1000),
					Duration.create(1, TimeUnit.SECONDS));
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		
		return (boolean) result;
	}
	
	public static ActorRef playBotGame(final String gameInfo, ActorRef player)
	{
		Object result;
		
		return null;
	}

	@Override
	public void onReceive(Object message) throws Exception
	{
		if (message instanceof Join)
		{
			onJoin(message);
		}
		else if (message instanceof Quit)
		{
			onQuit(message);
		}
		else if (message instanceof GetPlayer)
		{
			onGetPlayer((GetPlayer) message);
		}
		else if (message instanceof WaitForGame)
		{
			onWaitForGame((WaitForGame) message);
		}
		else if (message instanceof CancelWaiting)
		{
			onCancelWaiting((CancelWaiting) message);
		}
		else
		{
			unhandled(message);
		}
	}


	private void onJoin(Object message)
	{
		Join joinMessage = (Join) message;
		if (!players.containsKey(joinMessage.name))
		{
			ActorRef playerActor = Akka.system()
					.actorOf(Props.create(Player.class, joinMessage.name, joinMessage.in, joinMessage.out, playerRoom));
			players.put(joinMessage.name, playerActor);
			getSender().tell(new PlayerAccepted(), getSelf());
		}
		else
		{
			getSender().tell(new PlayerRejected(), getSelf());
		}
	}

	private void onQuit(Object message)
	{
		Quit quitMessage = (Quit) message;
		players.remove(quitMessage.name);
	}

	private void onGetPlayer(GetPlayer message)
	{
		getSender().tell(new ReturnPlayer(players.get(message.name)), getSelf());
	}
	
	private void onWaitForGame(WaitForGame message)
	{
		ActorRef player2;
		String gameInfo= message.gameInfo.getAsString();
		ActorRef player= message.player;
		if(false==waitingplayers.containsKey(gameInfo))
		{
			waitingplayers.put(gameInfo, player);
			return;
		}
		player2 = waitingplayers.remove(gameInfo);
		GameFactory director = GameFactory.getInstance();
		GameController gameController = director.createGame(gameInfo);
		ActorRef game = Akka.system().actorOf(Props.create(Game.class, player, player2, gameController, playerRoom));
		
		CreateGame msg= new CreateGame(game, true);
		player.tell(msg, getSelf());
		msg= new CreateGame(game, false);
		player2.tell(msg, getSelf());
	}
	
	private void onCancelWaiting(CancelWaiting message)
	{
		if(false==waitingplayers.containsValue(message.player))
		{
			getSender().tell(false, getSelf());
		}
		waitingplayers.remove(message.gameInfo);
		getSender().tell(true, getSelf());
	}
}
