package pwrrgmp2017.go.client.login;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController
{
	private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

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
		int serverPort;

		try
		{
			serverPort = Integer.parseInt(serverPortField.getText());
		}
		catch (NumberFormatException e)
		{
			LOGGER.warning("Port must be a number!");
			return;
		}

		if (serverPort < 1024)
		{
			LOGGER.warning("Port must be greater than 1023!");
			return;
		}
	}

	@FXML
	public void initialize()
	{

	}

}
