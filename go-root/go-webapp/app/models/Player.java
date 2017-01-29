package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import models.msgs.CancelInvitation;
import models.msgs.ConfirmInvitation;
import models.msgs.Invite;
import models.msgs.Quit;
import models.msgs.RespondToInvitation;
import models.msgs.UnknownMessage;
import play.Logger;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.WebSocket;
import pwrrgmp2017.go.game.factory.GameInfo;
import pwrrgmp2017.go.game.factory.GameInfo.RulesType;

public class Player extends UntypedActor {
	private final String name;
	private final WebSocket.In<JsonNode> in;
	private final WebSocket.Out<JsonNode> out;
	private final ActorRef playerRoom;
	private Player.State state;
	private Invite invitation;
	private ActorRef currentGame;
	
	public enum State {
		QUITTED,
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
		this.invitation = null;
		this.currentGame = null;
		
		// Convert messages to objects
		in.onMessage(new Callback<JsonNode>() {
            @Override
            public void invoke(JsonNode event) {
                try {
                	String messageType = event.get("messageType").asText();                
                	if (messageType.equals("quit")) {
                		getSelf().tell(new Quit(name), getSelf());
                	} else if (messageType.equals("invitationResponse")) {
                		boolean isAccepted = event.get("isAccepted").asBoolean();
                		String reason = event.get("reason").asText();
                		getSelf().tell(new RespondToInvitation(isAccepted, reason), getSelf());
                	} else if (messageType.equals("invitation")) {
                		String invitingPlayerName = event.get("invitingPlayerName").asText();
                		String invitedPlayerName = event.get("invitedPlayerName").asText();
                		double komi = event.get("komi").asDouble();
                		int boardSize = event.get("boardSize").asInt();
                		boolean isBot = event.get("isBot").asBoolean();
                		GameInfo gameInfo = new GameInfo(boardSize, (float) komi, RulesType.JAPANESE, isBot);
                		ActorRef invitingPlayer = getSelf();
                		ActorRef invitedPlayer = PlayerRoom.tryGetPlayer(invitedPlayerName);
                		getSelf().tell(new Invite(invitingPlayerName, invitedPlayerName, invitingPlayer, invitedPlayer, gameInfo), getSelf());
                	} else if (messageType.equals("cancelInvitation")) {
                		getSelf().tell(new CancelInvitation(), getSelf());
                	}
                	else {
                		getSelf().tell(new UnknownMessage(event.asText()), getSelf());
                	}
                }
                catch (Exception e) {
                    Logger.error("invokeError");
                }
            }
        });

        in.onClose(new Callback0() {
            @Override
            public void invoke() {
            	getSelf().tell(new Quit(name), getSelf());
            	playerRoom.tell(new Quit(name), getSelf());
            }
        });
	}
	
	// Player logic
	@Override
	public void onReceive(Object message) throws Exception {
		// Clean up a little bit
		if (invitation != null && (state != State.INVITED || state != State.INVITING)) {
			invitation = null;
		}
		if (currentGame != null && state != State.PLAYING) {
			currentGame = null;
		}
		
		if (message instanceof UnknownMessage) {
			Logger.warn("Player " + name + " received an unknown message: " + ((UnknownMessage) message).message);
		} else if (message instanceof Invite) {
			onInvitation((Invite) message);
		} else if (message instanceof RespondToInvitation) {
			onInvitationResponse((RespondToInvitation) message);
		} else if (message instanceof ConfirmInvitation) {
			onConfirmInvitation((ConfirmInvitation) message);
		} else if (message instanceof CancelInvitation) {
			onCancelInvitation((CancelInvitation) message);
		}
		else {
			unhandled(message);
		}
		
	}
	
	private void onInvitation(Invite message) {
		if (getSender() == getSelf()) {
			// Client has sent an invitation
			if (this.state != State.IN_SETTINGS) {
				Logger.warn("Player " + name + " sent an invitation but is in a wrong state.");
				return;
			}
			
			this.invitation = (Invite) message;
			if (this.invitation.invitedPlayer == null) {
				// The invited player does not exist, so we immediately send a negative response to the client
				ObjectNode json = Json.newObject();
				json.put("type", "invitationResponse");
				json.put("isAccepted", false);
				json.put("reason", "The player " + invitation.invitedPlayerName + " could not be found.");
				out.write(json);
				invitation = null;
				return;
			}
			
			// We tell the invited player about the invitation and wait for his response
			this.state = State.INVITING;
			invitation.invitedPlayer.tell(invitation, getSelf());
		} else {
			// We received an invitation from someone else
			
			if (this.state != State.IN_SETTINGS) {
				// We are busy and can't respond to the invitation
				RespondToInvitation response = new RespondToInvitation(false,
						"The invited player is not waiting for an invitation at the moment.");
				getSender().tell(response, getSelf());
				return;
			}
			
			// We are invited, the client must respond
			this.state = State.INVITED;
			this.invitation = message;
			
			// Send the invitation to client
			ObjectNode json = Json.newObject();
			json.put("type", "invitation");
			json.put("invitingPlayerName", invitation.invitingPlayerName);
			json.put("invitedPlayerName", invitation.invitedPlayerName);
			json.put("komi", invitation.gameInfo.getKomiValue());
			json.put("boardSize", invitation.gameInfo.getBoardSize());
			json.put("isBot", invitation.gameInfo.getIsBot());
			out.write(json);
		}
	}
	
	private void onInvitationResponse(RespondToInvitation message) {
		// We must be invited or inviting, otherwise we ignore the message
		if (this.state != State.INVITED && this.state != State.INVITING) {
			Logger.warn("Player " + name + " received an invitation response but is in a wrong state.");
			return;
		}

		if (this.state == State.INVITING) {
			if (getSender() == invitation.invitedPlayer) {
				// We received a response to our invitation
				
				// Send the response to the client
				ObjectNode json = Json.newObject();
				json.put("type", "invitationResponse");
				json.put("isAccepted", message.isAccepted);
				json.put("reason", message.reason);
				out.write(json);
				
				if (message.isAccepted) {
					// We create a new game here
					// this.state = State.PLAYING; //TODO
					this.state = State.IN_SETTINGS;
				} else {
					// We just come back to the previous state
					this.state = State.IN_SETTINGS;
				}
			} else if (getSender() == getSelf()) {
				Logger.warn("Player " + name + " (inviting) received an invitation response but is in a wrong state.");
			} else {
				Logger.warn("Player " + name + " (inviting) received an invitation response from an unknown source.");
			}
		} else {
			if (getSender() == getSelf()) {
				// The client responded to the invitation
				
				// Tell the inviting player about the response
				invitation.invitingPlayer.tell(message, getSelf());
				
				if (!message.isAccepted) {
					this.state = State.IN_SETTINGS;
				}
				
				// Wait for confirmation
			} else {
				Logger.warn("Player " + name + " (invited) received an invitation reponse from an unknown source.");
			}
		}
	}
	
	private void onConfirmInvitation(ConfirmInvitation message) {
		if (this.state != State.INVITED) {
			Logger.warn("Player " + name + " received an invitation confirmation but is in a wrong state.");
			return;
		}
		
		if (getSender() != invitation.invitingPlayer) {
			Logger.warn("Player " + name + " received an invitation confirmation from wrong sender.");
			return;
		}
		
		// We are playing the game
		//TODO
		//this.state = State.PLAYING;
		//this.game = message.game;
		
		ObjectNode json = Json.newObject();
		json.put("type", "confirmInvitation");
		out.write(json);
		this.state = State.IN_SETTINGS;
	}
	
	private void onCancelInvitation(CancelInvitation message) {
		
		if (this.state == State.PLAYING) {
			// Client cancelled too late
			ObjectNode json = Json.newObject();
			json.put("type", "cancelInvitationResponse");
			json.put("success", false);
			out.write(json);
		}
		
		if (this.state != State.INVITED && this.state != State.INVITING) {
			Logger.warn("Player " + name + " received an invitation cancel but is in a wrong state.");
			return;
		}
		
		if (getSender() == getSelf()) {
			// Client sent the cancel
			if (state == State.INVITING) {
				// Tell the invited player about that
				invitation.invitedPlayer.tell(message, getSelf());
				this.state = State.IN_SETTINGS;
				
				// Notify about success
				ObjectNode json = Json.newObject();
				json.put("type", "cancelInvitationResponse");
				json.put("success", true);
				out.write(json);
			} else {
				Logger.warn("Player " + name + " sent an invitation cancel but is in a wrong state.");
				return;
			}
		} else if (getSender() == invitation.invitingPlayer) {
			// The inviting player cancelled the invitation
			if (state == State.INVITED) {
				// Notify the client about that
				ObjectNode json = Json.newObject();
				json.put("type", "cancelInvitation");
				out.write(json);
				
				this.state = State.IN_SETTINGS;
			} else {
				Logger.warn("Player " + name + " received an invitation cancel but is in a wrong state.");
				return;
			}
		} else {
			Logger.warn("Player " + name + " received an invitation cancel from an unknown source.");
		}
	}
}
