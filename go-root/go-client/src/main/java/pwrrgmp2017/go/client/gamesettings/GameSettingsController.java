package pwrrgmp2017.go.client.gamesettings;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pwrrgmp2017.go.client.ClientMain;
import pwrrgmp2017.go.client.ServerConnection;
import pwrrgmp2017.go.client.gameboard.GameBoardController;
import pwrrgmp2017.go.clientserverprotocol.CancelWaitingProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.CancelWaitingResponseProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ConfirmationProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ExitProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.InvitationProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.InvitationResponseProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.PlayBotGameProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.PlayerFoundProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.UnknownProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.WaitForGameProtocolMessage;
import pwrrgmp2017.go.game.factory.GameInfo;
import pwrrgmp2017.go.game.factory.GameInfo.RulesType;

/**
 * Controller for the Game Settings window.
 */
public class GameSettingsController implements Observer
{
	/**
	 * Button for disconnecting from the server.
	 */
	@FXML
	private Button disconnectButton;

	/**
	 * Button setting the settings to default values.
	 */
	@FXML
	private Button defaultButton;

	/**
	 * Button sending an invitation to another player.
	 */
	@FXML
	private Button inviteButton;

	/**
	 * Button searching for any players waiting to play with the same game
	 * settings.
	 */
	@FXML
	private Button searchButton;

	/**
	 * Choice box defining the board size.
	 */
	@FXML
	private ChoiceBox<String> boardSizeChoiceBox;

	/**
	 * Group of radio buttons defining the game rules.
	 */
	@FXML
	private ToggleGroup gameRules;

	/**
	 * Radio button for Chinese rules.
	 * 
	 * @see gameRules
	 */
	@FXML
	private RadioButton chineseRadioButton;

	/**
	 * Radio button for Japanese rules.
	 * 
	 * @see gameRules
	 */
	@FXML
	private RadioButton japaneseRadioButton;

	/**
	 * The main pane of the window.
	 */
	@FXML
	private Pane gameSettingsPane;

	/**
	 * Field with the value of Komi.
	 */
	@FXML
	private TextField komiField;

	/**
	 * Button which starts the game with a bot.
	 */
	@FXML
	private Button playWithBot;

	/**
	 * Label with a current status of the controller (f.e. waiting for
	 * invitation, searching for player, etc.)
	 */
	@FXML
	private Label statusLabel;

	/**
	 * Button which cancels waiting for invitation or other process.
	 */
	@FXML
	private Button cancelButton;

	/**
	 * Label with the name of the player.
	 */
	@FXML
	private Label nameLabel;

	/**
	 * The player name.
	 */
	private String playerName;

	/**
	 * Connection to the server (running thread).
	 */
	private ServerConnection serverConnection;

	/**
	 * Our invitation sent to another player or invitation sent to us.
	 */
	private InvitationProtocolMessage invitation;

	/**
	 * Flag specifying if we are currently invited by another player.
	 */
	private volatile boolean areWeInvited;

	/**
	 * Flag specifying if the user accepted the invitation sent by another
	 * player.
	 */
	private volatile boolean didWeAcceptInvitation;

	/**
	 * Flag specifying if we are currently waiting for response from the server
	 * if our accepted invitation was not cancelled.
	 */
	private volatile boolean waitingForResponse;

	/**
	 * Information about the game we are waiting for (related to
	 * {@link #searchButton}, not invitations).
	 */
	private volatile GameInfo waitingGame;

	/**
	 * Initialises the necessary data for the controller, should be called
	 * during loading of the scene.
	 * 
	 * @param serverConnection
	 *            already established connection to the server
	 * @param playerName
	 *            name of the already logged in player
	 */
	public void initData(ServerConnection serverConnection, String playerName)
	{
		this.playerName = playerName;
		this.serverConnection = serverConnection;
		serverConnection.addObserver(this);
		serverConnection.startReceiving();
		nameLabel.setText(playerName);
		invitation = null;
		areWeInvited = false;
		didWeAcceptInvitation = false;
		waitingForResponse = false;
		waitingGame = null;
	}

	/**
	 * JavaFX specific initialisation.
	 */
	@FXML
	public void initialize()
	{
		defaultButton.setDisable(false);
		inviteButton.setDisable(false);
		playWithBot.setDisable(false);
		searchButton.setDisable(false);
		cancelButton.setDisable(true);
		boardSizeChoiceBox.getItems().add("19x19");
		boardSizeChoiceBox.getItems().add("13x13");
		boardSizeChoiceBox.getItems().add("9x9");

		handleDefault();
		statusLabel.setText("Waiting for invitation.");
	}

	/**
	 * Action on pressing the {@link #searchButton}.
	 * <p>
	 * Sets the {@link #waitingGame}, sends the appropriate message to the
	 * server and disables controls.
	 */
	@FXML
	protected void handleSearch()
	{
		waitingGame = createGameInfo(false);

		serverConnection.send(new WaitForGameProtocolMessage(waitingGame).getFullMessage());
		statusLabel.setText("Searching for player.");
		disableSettings();
		cancelButton.setDisable(false);
		inviteButton.setDisable(true);
		searchButton.setDisable(true);
	}

	/**
	 * Action on pressing the {@link #playWithBot}.
	 * <p>
	 * Sends an appropriate message to the server and changes to scene to Game
	 * Board.
	 */
	@FXML
	protected void onPlayWithBot()
	{
		GameInfo gameInfo = createGameInfo(true);

		serverConnection.send(new PlayBotGameProtocolMessage(playerName, gameInfo).getFullMessage());

		moveToGameBoardScene(gameInfo, playerName, "Bot", true);
	}

	/**
	 * Action on pressing the {@link #defaultButton}.
	 * <p>
	 * Changes the settings controls to their default values.
	 */
	@FXML
	protected void handleDefault()
	{
		boardSizeChoiceBox.getSelectionModel().selectFirst();
		komiField.setText("6.5");
		japaneseRadioButton.setSelected(true);
	}

	/**
	 * Action on pressing the {@link #cancelButton}.
	 * <p>
	 * Sends an appropriate message to the server and disables/enables controls
	 * properly. The cancel button is enabled when we are waiting for a response
	 * for our invitation ({@link #inviteButton}) or we are searching for a
	 * player ({@link #searchButton}).
	 */
	@FXML
	protected void handleCancel()
	{
		if (waitingGame != null)
		{
			serverConnection.send(new CancelWaitingProtocolMessage().getFullMessage());
			cancelButton.setDisable(true);
			statusLabel.setText("Waiting for response from server...");
		}
		else
		{
			InvitationResponseProtocolMessage response = new InvitationResponseProtocolMessage(false,
					"Inviting player cancelled the invitation.");
			serverConnection.send(response.getFullMessage());

			statusLabel.setText("Waiting for invitation.");
			inviteButton.setDisable(false);
			searchButton.setDisable(false);
			cancelButton.setDisable(true);
			enableSettings();
		}
	}

	/**
	 * Action on pressing {@link #disconnectButton}.
	 * <p>
	 * Sends an appropriate message to the server, closes the connection and
	 * changes the scene to Login.
	 */
	@FXML
	protected void handleDisconnect()
	{
		ExitProtocolMessage message = new ExitProtocolMessage();
		serverConnection.deleteObserver(this);
		serverConnection.send(message.getFullMessage());
		serverConnection.close();
		ClientMain.moveToScene((Stage) gameSettingsPane.getScene().getWindow(), "login/Login.fxml");
	}

	/**
	 * Action on pressing {@link #inviteButton}.
	 * <p>
	 * Creates a dialog where the user can specify the name of the player, then
	 * it sends the appropriate message to the server, sets the proper fields
	 * ({@link #invitation} and disables/enables some controls.
	 */
	@FXML
	protected void handleInvite()
	{
		GameInfo gameInfo = createGameInfo(false);
		if (gameInfo == null)
		{
			return;
		}

		TextInputDialog dialog = new TextInputDialog("Second player");
		dialog.setTitle("Invite Player");
		dialog.setHeaderText("Invite Player");
		dialog.setContentText("Enter the name of the player you wish to invite: ");

		Optional<String> result = dialog.showAndWait();
		if (!result.isPresent())
		{
			return;
		}

		String invitedPlayerName = result.get();

		invitation = new InvitationProtocolMessage(playerName, invitedPlayerName, gameInfo);
		serverConnection.send(invitation.getFullMessage());

		statusLabel.setText("Waiting for response...");
		inviteButton.setDisable(true);
		searchButton.setDisable(true);
		cancelButton.setDisable(false);
		disableSettings();
	}
	
	/**
	 * Creates a GameInfo object based on the settings in GUI.
	 * @param isBot whether or not we are playing with bot
	 * @return GameInfo object if it was properly created, null otherwise
	 */
	private GameInfo createGameInfo(boolean isBot)
	{
		String boardSizeString = boardSizeChoiceBox.getSelectionModel().getSelectedItem();
		int boardSize = Integer.parseInt((boardSizeString.split("x"))[0]);

		float komiValue;
		try
		{
			komiValue = Float.parseFloat(komiField.getText());
			if (komiValue < 0)
			{
				throw new NumberFormatException();
			}
		}
		catch (NumberFormatException e)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Wrong game settings");
			alert.setContentText("Komi must be a nonnegative number.");
			alert.showAndWait();
			return null;
		}

		GameInfo.RulesType rulesType;
		switch (((RadioButton) gameRules.getSelectedToggle()).getText())
		{
		case "Japanese":
			rulesType = RulesType.JAPANESE;
			break;

		case "Chinese":
			rulesType = RulesType.CHINESE;
			break;

		default:
			rulesType = RulesType.JAPANESE;
		}

		return new GameInfo(boardSize, komiValue, rulesType, isBot);
	}

	/**
	 * Reacts to messages from the server.
	 */
	@Override
	public void update(Observable o, Object arg)
	{
		if (o instanceof ServerConnection)
		{
			if (arg instanceof IOException)
			{
				// Something bad happened!
				serverConnection.deleteObserver(this);
				Platform.runLater(() ->
				{
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText("Connection error");
					alert.setContentText(((IOException) arg).getMessage());
					alert.showAndWait();
					ClientMain.moveToScene((Stage) gameSettingsPane.getScene().getWindow(), "login/Login.fxml");
				});
			}
			else if (arg instanceof InvitationProtocolMessage)
			{
				// We were invited
				invitation = (InvitationProtocolMessage) arg;
				areWeInvited = true;
				waitingForResponse = true;
				Platform.runLater(() ->
				{
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Invitation");
					alert.setHeaderText("You were invited.");
					alert.setContentText("Player " + invitation.getFromPlayerName()
							+ " wants to play with you with the following settings:\n" + "Board size: "
							+ invitation.getGameInfo().getBoardSize() + "\nRules: "
							+ invitation.getGameInfo().getRulesType() + "\nKomi: "
							+ invitation.getGameInfo().getKomiValue() + "\n" + "Do you accept the invitation?");

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK)
					{
						didWeAcceptInvitation = true;
						InvitationResponseProtocolMessage response = new InvitationResponseProtocolMessage(true,
								"Player accepted.");
						serverConnection.send(response.getFullMessage());
					}
					else
					{
						InvitationResponseProtocolMessage response = new InvitationResponseProtocolMessage(false,
								"Player refused.");
						serverConnection.send(response.getFullMessage());
						invitation = null;
						didWeAcceptInvitation = false;
						areWeInvited = false;
					}

					waitingForResponse = false;
					// We set the flag that we are invited, we wait for the
					// response from the server if the inviting player is still
					// waiting for response
				});
			}
			else if (arg instanceof InvitationResponseProtocolMessage)
			{
				if (!areWeInvited)
				{
					// We received a response for our invitation
					InvitationResponseProtocolMessage message = (InvitationResponseProtocolMessage) arg;

					if (!message.getIsAccepted())
					{
						// We were refused invitation :(
						Platform.runLater(() ->
						{
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Invitation");
							alert.setHeaderText("Your invitation was refused.");
							alert.setContentText(message.getReason());
							alert.showAndWait();
							inviteButton.setDisable(false);
							searchButton.setDisable(false);
							cancelButton.setDisable(true);
							statusLabel.setText("Waiting for invitation.");
							enableSettings();
						});
						return;
					}

					// Send the message to server in order to confirm we are
					// playing
					serverConnection.send(message.getFullMessage());

					Platform.runLater(() ->
					{
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Invitation");
						alert.setHeaderText("Your invitation was accepted.");
						alert.setContentText("The game is about to start.");
						alert.showAndWait();
						// Transition to game
						moveToGameBoardScene(invitation.getGameInfo(), invitation.getFromPlayerName(),
								invitation.getToPlayerName(), true);
					});
				}
				else
				{
					// Wait until the player decides
					while (waitingForResponse)
					{
						try
						{
							Thread.sleep(1);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}

					// We received a response for their invitation
					if (didWeAcceptInvitation)
					{
						// We only care if we accepted the invitation
						InvitationResponseProtocolMessage theirResponse = (InvitationResponseProtocolMessage) arg;
						if (theirResponse.getIsAccepted())
						{
							// Transition to game
							Platform.runLater(() ->
							{
								moveToGameBoardScene(invitation.getGameInfo(), invitation.getFromPlayerName(),
										invitation.getToPlayerName(), false);
							});
						}
						else
						{
							Platform.runLater(() ->
							{
								Alert alert = new Alert(AlertType.ERROR);
								alert.setTitle("Invitation");
								alert.setHeaderText("Invitation refused.");
								alert.setContentText(theirResponse.getReason());
								alert.showAndWait();
							});
							invitation = null;
							areWeInvited = false;
							didWeAcceptInvitation = false;
						}
					}
				}
			}
			else if (arg instanceof PlayerFoundProtocolMessage)
			{
				PlayerFoundProtocolMessage message = (PlayerFoundProtocolMessage) arg;

				boolean areWeBlack = message.getIsYourColorBlack();
				String blackPlayerName = areWeBlack ? playerName : message.getOpponentName();
				String whitePlayerName = areWeBlack ? message.getOpponentName() : playerName;

				if (!areWeBlack)
				{
					serverConnection.send(new ConfirmationProtocolMessage().getFullMessage());
				}

				Platform.runLater(() ->
				{
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setHeaderText("Player found");
					alert.setContentText("The game is about to begin with player " + message.getOpponentName());
					alert.showAndWait();
					moveToGameBoardScene(waitingGame, blackPlayerName, whitePlayerName, areWeBlack);
				});
			}
			else if (arg instanceof CancelWaitingResponseProtocolMessage)
			{
				boolean isSuccess = ((CancelWaitingResponseProtocolMessage) arg).getIsSuccess();
				if (isSuccess)
				{
					Platform.runLater(() ->
					{
						statusLabel.setText("Waiting for invitation.");
						cancelButton.setDisable(true);
						searchButton.setDisable(false);
						inviteButton.setDisable(false);
						enableSettings();
					});
				}
				else
				{
					Platform.runLater(() ->
					{
						Alert alert = new Alert(AlertType.ERROR);
						alert.setHeaderText("Could not cancel waiting");
						alert.setContentText("The game was already found.");
						alert.showAndWait();

						cancelButton.setDisable(false);
					});
				}
			}
			else if (arg instanceof UnknownProtocolMessage)
			{
				UnknownProtocolMessage message = (UnknownProtocolMessage) arg;
				Platform.runLater(() ->
				{
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Unknown message");
					alert.setHeaderText("Server sent an unknown message");
					alert.setContentText(message.getFullMessage());
					alert.showAndWait();
				});
			}
		}

	}

	/**
	 * Disables the controls related to game settings.
	 */
	public void disableSettings()
	{
		japaneseRadioButton.setDisable(true);
		boardSizeChoiceBox.setDisable(true);
		komiField.setDisable(true);
		playWithBot.setDisable(true);
		defaultButton.setDisable(true);
	}
	
	/**
	 * Enables the controls related to game settings.
	 */
	public void enableSettings()
	{
		japaneseRadioButton.setDisable(false);
		boardSizeChoiceBox.setDisable(false);
		komiField.setDisable(false);
		playWithBot.setDisable(false);
		defaultButton.setDisable(false);
	}

	/**
	 * Changes the scene to game board.
	 * @param gameInfo information about the game
	 * @param blackPlayerName name of the black player
	 * @param whitePlayerName name of the white player
	 * @param isBlackPlayer flag if we are playing as black
	 * @return new root
	 */
	private Parent moveToGameBoardScene(GameInfo gameInfo, String blackPlayerName, String whitePlayerName,
			boolean isBlackPlayer)
	{
		FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("gameboard/GameBoard.fxml"));

		Parent newRoot = null;
		try
		{
			newRoot = loader.load();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}

		GameBoardController controller = loader.<GameBoardController>getController();
		controller.initData(serverConnection, gameInfo, blackPlayerName, whitePlayerName, isBlackPlayer);
		serverConnection.deleteObserver(this);

		Stage stage = (Stage) gameSettingsPane.getScene().getWindow();
		Scene scene = stage.getScene();
		if (scene == null)
		{
			scene = new Scene(newRoot);
			stage.setScene(scene);
		}
		else
		{
			stage.getScene().setRoot(newRoot);
		}

		stage.sizeToScene();
		stage.setResizable(true);

		return newRoot;
	}
}
