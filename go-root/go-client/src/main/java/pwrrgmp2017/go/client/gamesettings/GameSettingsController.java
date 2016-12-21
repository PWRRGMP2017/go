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
import javafx.scene.control.CheckBox;
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
import pwrrgmp2017.go.clientserverprotocol.ExitProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.InvitationProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.InvitationResponseProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.UnknownProtocolMessage;
import pwrrgmp2017.go.game.factory.GameInfo;
import pwrrgmp2017.go.game.factory.GameInfo.RulesType;

public class GameSettingsController implements Observer
{
	@FXML
	private Button disconnectButton;

	@FXML
	private Button defaultButton;

	@FXML
	private Button inviteButton;

	@FXML
	private Button searchButton;

	@FXML
	private ChoiceBox<String> boardSizeChoiceBox;

	@FXML
	private ToggleGroup gameRules;

	@FXML
	private Pane gameSettingsPane;

	@FXML
	private TextField komiField;

	@FXML
	private CheckBox botCheckBox;

	@FXML
	private Label statusLabel;

	@FXML
	private Button cancelButton;

	@FXML
	private RadioButton chineseRadioButton;

	@FXML
	private RadioButton japaneseRadioButton;

	@FXML
	private Label nameLabel;

	private String playerName;
	private ServerConnection serverConnection;
	private InvitationProtocolMessage invitation;

	private volatile boolean areWeInvited;
	private volatile boolean didWeAcceptInvitation;
	private volatile boolean waitingForResponse;

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
	}

	@FXML
	public void initialize()
	{
		defaultButton.setDisable(false);
		inviteButton.setDisable(false);
		searchButton.setDisable(true);
		cancelButton.setDisable(true);
		boardSizeChoiceBox.getItems().add("19x19");
		boardSizeChoiceBox.getItems().add("13x13");
		boardSizeChoiceBox.getItems().add("9x9");

		handleDefault();
		statusLabel.setText("Waiting for invitation.");
	}

	@FXML
	protected void handleDefault()
	{
		botCheckBox.setSelected(false);
		boardSizeChoiceBox.getSelectionModel().selectFirst();
		komiField.setText("6.5");
		japaneseRadioButton.setSelected(true);
	}

	@FXML
	protected void handleCancel()
	{
		InvitationResponseProtocolMessage response = new InvitationResponseProtocolMessage(false,
				"Inviting player cancelled the invitation.");
		serverConnection.send(response.getFullMessage());

		statusLabel.setText("Waiting for invitation.");
		inviteButton.setDisable(false);
		cancelButton.setDisable(true);
		enableSettings();
	}

	@FXML
	protected void handleDisconnect()
	{
		ExitProtocolMessage message = new ExitProtocolMessage();
		serverConnection.send(message.getFullMessage());
		serverConnection.close();
		ClientMain.moveToScene((Stage) gameSettingsPane.getScene().getWindow(), "login/Login.fxml");
	}

	@FXML
	protected void handleInvite()
	{
		GameInfo gameInfo = createGameInfo();
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
		cancelButton.setDisable(false);
		disableSettings();
	}

	private GameInfo createGameInfo()
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

		boolean isBot = botCheckBox.isSelected();

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

	@Override
	public void update(Observable o, Object arg)
	{
		if (o instanceof ServerConnection)
		{
			if (arg instanceof IOException)
			{
				// Something bad happened!
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
							// TODO Auto-generated catch block
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

	public void disableSettings()
	{
		japaneseRadioButton.setDisable(true);
		boardSizeChoiceBox.setDisable(true);
		komiField.setDisable(true);
		botCheckBox.setDisable(true);
		defaultButton.setDisable(true);
	}

	public void enableSettings()
	{
		japaneseRadioButton.setDisable(false);
		boardSizeChoiceBox.setDisable(false);
		komiField.setDisable(false);
		botCheckBox.setDisable(false);
		defaultButton.setDisable(false);
	}

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
