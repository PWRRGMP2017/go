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

/**
 * Controller for the Login window.
 */
public class LoginController implements Observer
{
	/**
	 * Server connection, the thread should not be started yet.
	 */
	private ServerConnection serverConnection;

	/**
	 * Field with the name of the player.
	 */
	@FXML
	private TextField playerNameField;

	/**
	 * Field with the address of the server.
	 */
	@FXML
	private TextField serverAddressField;

	/**
	 * Field with the server port.
	 */
	@FXML
	private TextField serverPortField;

	/**
	 * The button which exits the application.
	 */
	@FXML
	private Button exitButton;

	/**
	 * The button which starts to connect to the server.
	 */
	@FXML
	private Button connectButton;

	/**
	 * The main pane of the window.
	 */
	@FXML
	private Pane loginPane;

	/**
	 * Action on pressing the {@link #exitButton}.
	 * <p>
	 * It closes the window.
	 */
	@FXML
	protected void handleExit()
	{
		Stage stage = (Stage) exitButton.getScene().getWindow();
		stage.close();
	}
	
	/**
	 * Action on pressing the {@link #connectButton}.
	 * <p>
	 * It tries to connect to server using {@link #serverConnection}, after verifying the parameters.
	 */
	@FXML
	protected void handleConnect()
	{
		String playerName = playerNameField.getText();
		String serverAddress = serverAddressField.getText();
		String serverPort = serverPortField.getText();
		
		try
		{
			serverConnection = new ServerConnection(serverAddress, serverPort);
			serverConnection.connect();
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
		loginHandlerThread.setDaemon(true);
		loginHandlerThread.start();
	}

	/**
	 * JavaFX specific initialisation.
	 */
	@FXML
	public void initialize()
	{

	}

	/**
	 * Listens for events from {@link LoginHandler} after pressing the {@link #connectButton} and reacts in the proper way.
	 */
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

	/**
	 * Disables all controls in the window.
	 * <p>
	 * Is called after pressing the {@link connectButton}.
	 */
	private void disableControls()
	{
		loginPane.setDisable(true);
	}

	/**
	 * Enables all controls in the window.
	 * <p>
	 * Is called if the connection was not successful.
	 */
	private void enableControls()
	{
		loginPane.setDisable(false);
	}

	/**
	 * Changes the scene to Game Settings after successful login.
	 * @return new root
	 */
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
