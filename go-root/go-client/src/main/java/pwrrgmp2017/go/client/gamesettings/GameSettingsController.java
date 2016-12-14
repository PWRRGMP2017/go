package pwrrgmp2017.go.client.gamesettings;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pwrrgmp2017.go.client.ClientMain;
import pwrrgmp2017.go.client.ServerConnection;
import pwrrgmp2017.go.clientserverprotocol.ExitProtocolMessage;

public class GameSettingsController
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

	private String playerName;
	private ServerConnection serverConnection;

	public void initData(ServerConnection serverConnection, String playerName)
	{
		this.playerName = playerName;
		this.serverConnection = serverConnection;
	}

	@FXML
	public void initialize()
	{
		defaultButton.setDisable(true);
		inviteButton.setDisable(true);
		searchButton.setDisable(true);
		boardSizeChoiceBox.getItems().add("19x19");
		boardSizeChoiceBox.getItems().add("13x13");
		boardSizeChoiceBox.getItems().add("9x9");
		boardSizeChoiceBox.getSelectionModel().selectFirst();
	}

	@FXML
	protected void handleDisconnect()
	{
		ExitProtocolMessage message = new ExitProtocolMessage();
		serverConnection.send(message.getFullMessage());
		serverConnection.close();
		ClientMain.moveToScene((Stage) gameSettingsPane.getScene().getWindow(), "login/Login.fxml");
	}
}
