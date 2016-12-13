package pwrrgmp2017.go.client.login;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController
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

		if (playerName.isEmpty() || serverAddress.isEmpty() || serverPortField.getText().isEmpty())
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Error while parsing fields");
			alert.setContentText("All fields must be filled!");
			alert.showAndWait();
			return;
		}

		int serverPort;

		try
		{
			serverPort = Integer.parseInt(serverPortField.getText());
		}
		catch (NumberFormatException e)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Error while parsing fields");
			alert.setContentText("Port must be a number!");
			alert.showAndWait();
			return;
		}

		if (serverPort < 1024)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Error while parsing fields");
			alert.setContentText("Port must be greater than 1024!");
			alert.showAndWait();
			return;
		}
	}

	@FXML
	public void initialize()
	{

	}

}
