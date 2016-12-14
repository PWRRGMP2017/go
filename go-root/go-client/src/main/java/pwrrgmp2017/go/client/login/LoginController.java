package pwrrgmp2017.go.client.login;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pwrrgmp2017.go.client.ServerConnection;

public class LoginController implements Observer
{
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

		ServerConnection connection = null;
		try
		{
			connection = new ServerConnection(serverAddress, serverPort);
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

		LoginHandler loginHandler = new LoginHandler(connection, playerName);
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
				});
			}
			else
			{
				Platform.runLater(() ->
				{
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText("Server response");
					alert.setContentText("Login failed: " + loginHandler.getReason());
					alert.showAndWait();
				});
			}

			Platform.runLater(() ->
			{
				enableControls();
			});
			loginHandler.getServerConnection().close();
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

}
