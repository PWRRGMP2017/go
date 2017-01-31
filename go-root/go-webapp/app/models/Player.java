package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import models.msgs.AcceptTerritory;
import models.msgs.CancelInvitation;
import models.msgs.CancelWaiting;
import models.msgs.ConfirmInvitation;
import models.msgs.CreateGame;
import models.msgs.GameEnded;
import models.msgs.Invite;
import models.msgs.Move;
import models.msgs.Pass;
import models.msgs.PlayBotGame;
import models.msgs.Quit;
import models.msgs.RefreshBoard;
import models.msgs.Resign;
import models.msgs.RespondToInvitation;
import models.msgs.ResumeGame;
import models.msgs.SendBoard;
import models.msgs.UnknownMessage;
import models.msgs.WaitForGame;
import play.Logger;
import play.libs.Akka;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.WebSocket;
import pwrrgmp2017.go.game.factory.GameFactory;
import pwrrgmp2017.go.game.factory.GameInfo;
import pwrrgmp2017.go.game.factory.GameInfo.RulesType;

public class Player extends UntypedActor
{
	private final String name;
	private final WebSocket.In<JsonNode> in;
	private final WebSocket.Out<JsonNode> out;
	private final ActorRef playerRoom;
	private Player.State state;
	private Invite invitation;
	private ActorRef currentGame;
	private String gameInfo;

	public enum State
	{
		QUITTED, IN_SETTINGS, INVITING, INVITED, SEARCHING, PLAYING
	}

	public Player(final String name, WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out,
			final ActorRef playerRoom)
	{
		this.name = name;
		this.in = in;
		this.out = out;
		this.playerRoom = playerRoom;
		this.state = Player.State.IN_SETTINGS;
		this.invitation = null;
		this.currentGame = null;

		// Convert messages to objects
		in.onMessage(new Callback<JsonNode>()
		{
			@Override
			public void invoke(JsonNode event)
			{
				try
				{
					String messageType = event.get("type").asText();
					if (messageType.equals("quit"))
					{
						getSelf().tell(new Quit(name), getSelf());
					}
					else if (messageType.equals("invitationResponse"))
					{
						boolean isAccepted = event.get("isAccepted").asBoolean();
						String reason = event.get("reason").asText();
						getSelf().tell(new RespondToInvitation(isAccepted, reason), getSelf());
					}
					else if (messageType.equals("invitation"))
					{
						String invitingPlayerName = event.get("invitingPlayerName").asText();
						String invitedPlayerName = event.get("invitedPlayerName").asText();
						if (invitedPlayerName.equals(name))
						{
							ObjectNode json = Json.newObject();
							json.put("type", "invitationResponse");
							json.put("isAccepted", false);
							json.put("reason", "You can't invite yourself!");
							out.write(json);
							return;
						}
						double komi = event.get("komi").asDouble();
						int boardSize = event.get("boardSize").asInt();
						boolean isBot = event.get("isBot").asBoolean();
						GameInfo gameInfo = new GameInfo(boardSize, (float) komi, RulesType.JAPANESE, isBot);
						ActorRef invitingPlayer = getSelf();
						ActorRef invitedPlayer = PlayerRoom.tryGetPlayer(invitedPlayerName);
						getSelf().tell(new Invite(invitingPlayerName, invitedPlayerName, invitingPlayer, invitedPlayer,
								gameInfo), getSelf());
					}
					else if (messageType.equals("cancelInvitation"))
					{
						getSelf().tell(new CancelInvitation(), getSelf());
					}
					else if (messageType.equals("waitForGame"))
					{
						double komi = event.get("komi").asDouble();
						int boardSize = event.get("boardSize").asInt();
						boolean isBot = false;
						GameInfo gameInfo = new GameInfo(boardSize, (float) komi, RulesType.JAPANESE, isBot);
						ActorRef player = getSelf();
						getSelf().tell(new WaitForGame(player, gameInfo, name), getSelf());
					}
					else if (messageType.equals("stopWaiting"))
					{
						getSelf().tell(new CancelWaiting(getSelf(), null), getSelf());
					}
					else if (messageType.equals("move"))
					{
						int x = event.get("x").asInt();
						int y = event.get("y").asInt();
						getSelf().tell(new Move(x, y), getSelf());
					}
					else if (messageType.equals("pass"))
					{
						getSelf().tell(new Pass(), getSelf());
					}
					else if (messageType.equals("resign"))
					{
						getSelf().tell(new Resign(), getSelf());
					}
					else if (messageType.equals("acceptTerritory"))
					{
						getSelf().tell(new AcceptTerritory(), getSelf());
					}
					else if (messageType.equals("playWithBot"))
					{
						double komi = event.get("komi").asDouble();
						int boardSize = event.get("boardSize").asInt();
						boolean isBot = false;
						GameInfo gameInfo = new GameInfo(boardSize, (float) komi, RulesType.JAPANESE, isBot);
						ActorRef player = getSelf();
						getSelf().tell(new PlayBotGame(gameInfo, player), getSelf());
					}
					else if (messageType.equals("resumeGame"))
					{
						getSelf().tell(new ResumeGame(), getSelf());
					}
					else if (messageType.equals("refreshBoard"))
					{
						getSelf().tell(new RefreshBoard(), getSelf());
					}
					else
					{
						getSelf().tell(new UnknownMessage(event.asText()), getSelf());
					}
				}
				catch (Exception e)
				{
					Logger.error("invokeError");
					e.printStackTrace();
				}
			}
		});

		in.onClose(new Callback0()
		{
			@Override
			public void invoke()
			{
				out.close();
				getSelf().tell(new Quit(name), getSelf());
				playerRoom.tell(new Quit(name), getSelf());
			}
		});
	}

	// Player logic
	@Override
	public void onReceive(Object message) throws Exception
	{
		// Clean up a little bit
		if (invitation != null && state != State.INVITED && state != State.INVITING)
		{
			invitation = null;
		}
		if (currentGame != null && state != State.PLAYING)
		{
			currentGame = null;
		}

		if (message instanceof UnknownMessage)
		{
			Logger.warn("Player " + name + " received an unknown message: " + ((UnknownMessage) message).message);
		}
		else if (message instanceof Invite)
		{
			onInvitation((Invite) message);
		}
		else if (message instanceof RespondToInvitation)
		{
			onInvitationResponse((RespondToInvitation) message);
		}
		else if (message instanceof ConfirmInvitation)
		{
			onConfirmInvitation((ConfirmInvitation) message);
		}
		else if (message instanceof CancelInvitation)
		{
			onCancelInvitation((CancelInvitation) message);
		}
		else if (message instanceof Quit)
		{
			onQuit((Quit) message);
		}
		else if (message instanceof CancelWaiting)
		{
			onCancelWaiting((CancelWaiting) message);
		}
		else if (message instanceof WaitForGame)
		{
			onWaitForGame((WaitForGame) message);
		}
		else if (message instanceof RefreshBoard)
		{
			onRefreshBoard((RefreshBoard) message);
		}
		else if (message instanceof Resign)
		{
			onResign((Resign) message);
		}
		else if (message instanceof Pass)
		{
			onPass((Pass) message);
		}
		else if (message instanceof AcceptTerritory)
		{
			onAcceptTerritory((AcceptTerritory) message);
		}
		else if (message instanceof ResumeGame)
		{
			onResumeGame((ResumeGame) message);
		}
		else if (message instanceof PlayBotGame)
		{
			onPlayBotGame((PlayBotGame) message);
		}
		else if (message instanceof CreateGame)
		{
			onCreateGame((CreateGame) message);
		}
		else if (message instanceof Move)
		{
			onMove((Move) message);
		}
		else if (message instanceof SendBoard)
		{
			onSendBoard((SendBoard) message);
		}
		else if (message instanceof GameEnded)
		{
			onGameEnded((GameEnded) message);
		}
		else
		{
			unhandled(message);
		}
		

	}

	private void onGameEnded(GameEnded message)
	{
		if (state != State.PLAYING)
		{
			Logger.warn("Player " + name + " received game ended message but is in a wrong state.");
			return;
		}

		ObjectNode json = Json.newObject();
		json.put("type", "gameEnded");
		json.put("stats", message.stats);
		out.write(json);
		
		this.state = State.IN_SETTINGS;
	}

	private void onResumeGame(ResumeGame message)
	{
		if (state != State.PLAYING)
		{
			Logger.warn("Player " + name + " sent resume game message but is in a wrong state.");
			return;
		}

		currentGame.tell(message, getSelf());
	}

	private void onPass(Pass message)
	{
		if (state != State.PLAYING)
		{
			Logger.warn("Player " + name + " sent pass message but is in a wrong state.");
			return;
		}

		currentGame.tell(message, getSelf());
	}

	private void onMove(Move message)
	{
		if (state != State.PLAYING)
		{
			Logger.warn("Player " + name + " sent move message but is in a wrong state.");
			return;
		}

		currentGame.tell(message, getSelf());
	}

	private void onSendBoard(SendBoard message)
	{
		out.write(message.json);
	}

	private void onInvitation(Invite message)
	{
		if (getSender() == getSelf())
		{
			// Client has sent an invitation
			if (this.state != State.IN_SETTINGS)
			{
				Logger.warn("Player " + name + " sent an invitation but is in a wrong state.");
				return;
			}

			this.invitation = message;
			if (this.invitation.invitedPlayer == null)
			{
				// The invited player does not exist, so we immediately send a
				// negative response to the client
				ObjectNode json = Json.newObject();
				json.put("type", "invitationResponse");
				json.put("isAccepted", false);
				json.put("reason", "The player " + invitation.invitedPlayerName + " could not be found.");
				out.write(json);
				invitation = null;
				return;
			}

			// We tell the invited player about the invitation and wait for his
			// response
			this.state = State.INVITING;
			invitation.invitedPlayer.tell(invitation, getSelf());
		}
		else
		{
			// We received an invitation from someone else

			if (this.state != State.IN_SETTINGS)
			{
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

	private void onInvitationResponse(RespondToInvitation message)
	{
		// We must be invited or inviting, otherwise we ignore the message
		if (this.state != State.INVITED && this.state != State.INVITING)
		{
			Logger.warn("Player " + name + " received an invitation response but is in a wrong state.");
			return;
		}

		if (this.state == State.INVITING)
		{
			if (getSender() == invitation.invitedPlayer)
			{
				// We received a response to our invitation

				// Send the response to the client
				ObjectNode json = Json.newObject();
				json.put("type", "invitationResponse");
				json.put("isAccepted", message.isAccepted);
				json.put("reason", message.reason);
				out.write(json);

				if (message.isAccepted)
				{
					// We create a new game here
					this.currentGame = Akka.system()
							.actorOf(Props.create(Game.class, invitation.invitingPlayer, invitation.invitedPlayer,
									GameFactory.getInstance().createGame(invitation.gameInfo.getAsString())));
					this.state = State.PLAYING;

					// Send the confirmation to the opponent
					invitation.invitedPlayer.tell(new ConfirmInvitation(currentGame), getSelf());
					
					this.currentGame.tell(new RefreshBoard(), invitation.invitedPlayer);
					this.currentGame.tell(new RefreshBoard(), invitation.invitingPlayer);
				}
				else
				{
					// We just come back to the previous state
					this.state = State.IN_SETTINGS;
				}
			}
			else if (getSender() == getSelf())
			{
				Logger.warn("Player " + name + " (inviting) received an invitation response but is in a wrong state.");
			}
			else
			{
				Logger.warn("Player " + name + " (inviting) received an invitation response from an unknown source.");
			}
		}
		else
		{
			if (getSender() == getSelf())
			{
				// The client responded to the invitation

				// Tell the inviting player about the response
				invitation.invitingPlayer.tell(message, getSelf());

				if (!message.isAccepted)
				{
					this.state = State.IN_SETTINGS;
				}

				// Wait for confirmation
			}
			else
			{
				Logger.warn("Player " + name + " (invited) received an invitation reponse from an unknown source.");
			}
		}
	}

	private void onConfirmInvitation(ConfirmInvitation message)
	{
		if (this.state != State.INVITED)
		{
			Logger.warn("Player " + name + " received an invitation confirmation but is in a wrong state.");
			return;
		}

		if (getSender() != invitation.invitingPlayer)
		{
			Logger.warn("Player " + name + " received an invitation confirmation from wrong sender.");
			return;
		}

		// We are playing the game
		this.state = State.PLAYING;
		this.currentGame = message.game;

		ObjectNode json = Json.newObject();
		json.put("type", "confirmInvitation");
		out.write(json);
	}

	private void onCancelInvitation(CancelInvitation message)
	{

		if (this.state == State.PLAYING)
		{
			// Client cancelled too late
			ObjectNode json = Json.newObject();
			json.put("type", "cancelInvitationResponse");
			json.put("success", false);
			out.write(json);
		}

		if (this.state != State.INVITED && this.state != State.INVITING)
		{
			Logger.warn("Player " + name + " received an invitation cancel but is in a wrong state.");
			return;
		}

		if (getSender() == getSelf())
		{
			// Client sent the cancel
			if (state == State.INVITING)
			{
				// Tell the invited player about that
				invitation.invitedPlayer.tell(message, getSelf());
				this.state = State.IN_SETTINGS;

				// Notify about success
				ObjectNode json = Json.newObject();
				json.put("type", "cancelInvitationResponse");
				json.put("success", true);
				out.write(json);
			}
			else
			{
				Logger.warn("Player " + name + " sent an invitation cancel but is in a wrong state.");
				return;
			}
		}
		else if (getSender() == invitation.invitingPlayer)
		{
			// The inviting player cancelled the invitation
			if (state == State.INVITED)
			{
				// Notify the client about that
				ObjectNode json = Json.newObject();
				json.put("type", "cancelInvitation");
				out.write(json);

				this.state = State.IN_SETTINGS;
			}
			else
			{
				Logger.warn("Player " + name + " received an invitation cancel but is in a wrong state.");
				return;
			}
		}
		else
		{
			Logger.warn("Player " + name + " received an invitation cancel from an unknown source.");
		}
	}

	private void onQuit(Quit message)
	{
		if (invitation != null)
		{
			if (state == State.INVITED)
			{
				invitation.invitingPlayer.tell(
						new RespondToInvitation(false, invitation.invitedPlayerName + " has disconnected."), getSelf());
			}
			else if (state == State.INVITING)
			{
				invitation.invitedPlayer.tell(new CancelInvitation(), getSelf());
			}
			invitation = null;
		}

		if (currentGame != null && state == State.PLAYING)
		{
			// tell GameEnd to the other player
			currentGame.tell(new Quit(name), getSelf());
			currentGame = null;
		}

		this.state = State.QUITTED;
	}

	private void onRefreshBoard(RefreshBoard message)
	{
		if (state != State.PLAYING)
		{
			Logger.warn("Player " + name + " sent refresh board message but is in wrong state.");
			return;
		}

		currentGame.tell(message, getSelf());
	}

	private void onCancelWaiting(CancelWaiting message)
	{
		if(state != State.SEARCHING)
		{
			return;
		}
		
		ObjectNode json = Json.newObject();
		json.put("type", "cancelWaitingResponse");
		if(PlayerRoom.cancelWaiting(new CancelWaiting(getSelf(), gameInfo))==true)
		{
			state=State.IN_SETTINGS;
			json.put("success", true);
		}
		else
		{
			json.put("success", false);
		}
		out.write(json);
	}

	private void onWaitForGame(WaitForGame message)
	{
		if(state != State.IN_SETTINGS)
		{
			Logger.warn("Player " + name + " sent wait for game message but is in wrong state.");
			return;
		}
		state=State.SEARCHING;
		this.gameInfo=message.gameInfo.getAsString();
		playerRoom.tell(message, getSelf());
	}

	private void onAcceptTerritory(AcceptTerritory message)
	{
		if (state != State.PLAYING)
		{
			Logger.warn("Player " + name + " sent refresh board message but is in a wrong state.");
			return;
		}

		currentGame.tell(message, getSelf());
	}

	private void onPlayBotGame(PlayBotGame message)
	{
		if(state!=State.IN_SETTINGS)
		{
			return;
		}
		state=State.PLAYING;
		playerRoom.tell(message, getSelf());
	}
	
	private void onResign(Resign message)
	{
		if (state != State.PLAYING)
		{
			Logger.warn("Player " + name + " sent resign message but is in a wrong state.");
			return;
		}

		currentGame.tell(message, getSelf());
	}
	
	private void onCreateGame(CreateGame message)
	{
		ObjectNode json = Json.newObject();
		json.put("type", "createGame");
		json.put("isBlack", message.isBlack);
		json.put("opponentName", message.opponentName);
		out.write(json);
		this.currentGame=message.game;
		currentGame.tell(new RefreshBoard(), getSelf());
		state=State.PLAYING;
	}
}
