package models;

import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import models.msgs.Quit;
import models.msgs.UnknownMessage;
import play.Logger;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.WebSocket;

public class Player extends UntypedActor {
	private final String name;
	private final WebSocket.In<JsonNode> in;
	private final WebSocket.Out<JsonNode> out;
	private final ActorRef playerRoom;
	private Player.State state;
	
	public enum State {
		IN_SETTINGS,
		INVITING,
		INVITED,
		SEARCHING,
		PLAYING
	}
	
	public Player(final String name, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out, final ActorRef playerRoom) {
		this.name = name;
		this.in = in;
		this.out = out;
		this.playerRoom = playerRoom;
		this.state = Player.State.IN_SETTINGS;
		
		// Convert messages to objects
		in.onMessage(new Callback<JsonNode>()
        {
            @Override
            public void invoke(JsonNode event)
            {
                try
                {
                	String messageType = event.get("messageType").asText();                
                	if (messageType.equals("quit")) {
                		getSelf().tell(new Quit(name), getSelf());
                	} else {
                		getSelf().tell(new UnknownMessage(event.asText()), getSelf());
                	}
                }
                catch (Exception e)
                {
                    Logger.error("invokeError");
                }
                
            }
        });

        in.onClose(new Callback0()
        {
            @Override
            public void invoke()
            {
            	playerRoom.tell(new Quit(name), getSelf());
            }
        });
	}
	
	// Player logic
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof UnknownMessage) {
			Logger.warn("Player " + name + " received an unknown message: " + ((UnknownMessage) message).message);
		}
		
	}
}
