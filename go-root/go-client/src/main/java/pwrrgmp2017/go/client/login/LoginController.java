package pwrrgmp2017.go.client.login;

import java.io.IOException;
import java.security.InvalidParameterException;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pwrrgmp2017.go.client.ClientMain;
import pwrrgmp2017.go.client.ServerConnection;
import pwrrgmp2017.go.client.gamesettings.GameSettingsController;

public class LoginController implements Observer
{
	private ServerConnection serverConnection;

	@FXML
	private TextField playerNameField;

	@FXML
	private TextField serverAddressField;

	@FXML
	private TextField serverPortField;

	@FXML
	private Button exitButton;

	@FXML
	private Button connectButton;

	@FXML
	private Pane loginPane;

	@FXML
	protected void handleExit()
	{
		Stage stage = (Stage) exitButton.getScene().getWindow();
		stage.close();
	}

	@FXML
	protected void handleConnect()
	{
		String playerName = playerNameField.getText();
		String serverAddress = serverAddressField.getText();
		String serverPort = serverPortField.getText();

		try
		{
			serverConnection = new ServerConnection(serverAddress, serverPort);
		}
		catch (InvalidParameterException e)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Error while parsing fields");
			alert.setContentText(e.getMessage());
			alert.showAndWait();
			return;
		}
		catch (IOException e)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Error while connecting to server");
			alert.setContentText(e.getMessage());
			alert.showAndWait();
			return;
		}

		disableControls();

		LoginHandler loginHandler = new LoginHandler(serverConnection, playerName);
		loginHandler.addObserver(this);
		Thread loginHandlerThread = new Thread(loginHandler);
		loginHandlerThread.start();
	}

	@FXML
	public void initialize()
	{

	}

	@Override
	public void update(Observable o, Object arg)
	{
		if (o instanceof LoginHandler)
		{
			LoginHandler loginHandler = (LoginHandler) o;
			if (loginHandler.getIsLoginSuccessful())
			{
				Platform.runLater(() ->
				{
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setHeaderText("Server response");
					alert.setContentText("Logged successfully.");
					alert.showAndWait();
					moveToGameSettingsScene();
				});
			}
			else
			{
				serverConnection.close();
				serverConnection = null;
				Platform.runLater(() ->
				{
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText("Server response");
					alert.setContentText("Login failed: " + loginHandler.getReason());
					alert.showAndWait();
					enableControls();
				});
			}
		}

	}

	private void disableControls()
	{
		loginPane.setDisable(true);
	}

	private void enableControls()
	{
		loginPane.setDisable(false);
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
		controller.initData(serverConnection, playerNameField.getText());

		Stage stage = (Stage) loginPane.getScene().getWindow();
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
