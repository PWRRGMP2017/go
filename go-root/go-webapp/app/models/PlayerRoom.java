package models;

import static akka.pattern.Patterns.ask;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import models.msgs.GetPlayer;
import models.msgs.Join;
import models.msgs.PlayerAccepted;
import models.msgs.PlayerRejected;
import models.msgs.Quit;
import models.msgs.ReturnPlayer;
import play.libs.Akka;
import play.mvc.WebSocket;
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
	
	public static ActorRef waitForGame(final String gameInfo, final ActorRef player)
	{
		Object result;
		try
		{
			
		}
		catch (Exception e) 
		{
			
		}
		return null;
	}
	
	public static ActorRef cancelWaiting(final String gameInfo, final ActorRef player)
	{
		Object result;
		try
		{
			
		}
		catch (Exception e) 
		{
			
		}
		return null;
	}
	
	public static ActorRef playBotGame(final String gameInfo, ActorRef player)
	{
		Object result;
		try
		{
			
		}
		catch (Exception e) 
		{
			
		}
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
}
