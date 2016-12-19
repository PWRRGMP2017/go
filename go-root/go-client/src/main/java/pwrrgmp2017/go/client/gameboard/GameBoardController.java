package pwrrgmp2017.go.client.gameboard;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pwrrgmp2017.go.client.ClientMain;
import pwrrgmp2017.go.client.ServerConnection;
import pwrrgmp2017.go.client.gamesettings.GameSettingsController;
import pwrrgmp2017.go.clientserverprotocol.ConfirmationProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ResignProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.UnknownProtocolMessage;
import pwrrgmp2017.go.game.factory.GameInfo;

public class GameBoardController implements Observer
{

	@FXML
	private Pane mainPane;

	@FXML
	private Label blackPlayerLabel;

	@FXML
	private Label whitePlayerLabel;

	@FXML
	private Label stateLabel;

	@FXML
	private TextArea statsTextArea;

	@FXML
	private GridPane boardPane;

	@FXML
	private Button resignButton;

	@FXML
	private Button passButton;

	@FXML
	private Button acceptButton;

	private GameInfo gameInfo;
	private ServerConnection serverConnection;
	private String thisPlayerName;

	public void initData(ServerConnection serverConnection, GameInfo gameInfo, String blackPlayerName,
			String whitePlayerName, boolean isThisPlayerBlack)
	{
		this.serverConnection = serverConnection;
		serverConnection.addObserver(this);
		this.gameInfo = gameInfo;

		// TODO create GameController here

		// TODO generate board pane here

		thisPlayerName = isThisPlayerBlack ? blackPlayerName : whitePlayerName;
		blackPlayerLabel.setText(blackPlayerName);
		whitePlayerLabel.setText(whitePlayerName);
		passButton.setDisable(true);
		acceptButton.setDisable(true);
	}

	@FXML
	public void initialize()
	{

	}

	@FXML
	protected void handleResign()
	{
		// Send the resign message
		ResignProtocolMessage message = new ResignProtocolMessage("Player resigned.");
		serverConnection.send(message.getFullMessage());

		// Move to GameSettings
		moveToGameSettingsScene();
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
					ClientMain.moveToScene((Stage) mainPane.getScene().getWindow(), "login/Login.fxml");
				});
			}
			else if (arg instanceof ResignProtocolMessage)
			{
				// The second player has resigned
				Platform.runLater(() ->
				{
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setHeaderText("Resignation");
					alert.setContentText(((ResignProtocolMessage) arg).getReason());
					alert.showAndWait();
					// Show results
					serverConnection.send(new ConfirmationProtocolMessage().getFullMessage());
					moveToGameSettingsScene();
				});
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

	private Parent moveToGameSettingsScene()
	{
		FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("gamesettings/GameSettings.fxml"));

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

		GameSettingsController controller = loader.<GameSettingsController>getController();
		controller.initData(serverConnection, thisPlayerName);
		serverConnection.deleteObserver(this);

		Stage stage = (Stage) mainPane.getScene().getWindow();
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
		stage.setResizable(false);

		return newRoot;
	}

}
