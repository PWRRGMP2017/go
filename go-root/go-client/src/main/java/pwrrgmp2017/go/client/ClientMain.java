package pwrrgmp2017.go.client;

import java.io.IOException;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Runs the client
 */
public class ClientMain extends Application
{
	private static final Logger LOGGER = Logger.getLogger(ClientMain.class.getName());

	public static void main(String[] args)
	{
		if (args.length != 2)
		{
			printUsage();
			return;
		}

		String hostname = args[0];
		int port;
		try
		{
			port = Integer.parseUnsignedInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			LOGGER.severe("Port must be a number: " + args[1]);
			return;
		}

		if (port < 5001)
		{
			LOGGER.severe("Port must be greater than 5000: " + port);
			return;
		}

		try
		{
			new ServerConnection(hostname, port);
		}
		catch (IOException e)
		{
			LOGGER.severe("Could not connect to server: " + e.getMessage());
			return;
		}

		launch(args);
	}

	private static void printUsage()
	{
		System.out.println("usage: java go-client.jar hostname port");
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		primaryStage.setTitle("Hello World!");
		Button btn = new Button();
		btn.setText("Say 'Hello World'");
		btn.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent event)
			{
				System.out.println("Hello World!");
			}
		});

		StackPane root = new StackPane();
		root.getChildren().add(btn);
		primaryStage.setScene(new Scene(root, 300, 250));
		primaryStage.show();
	}
}
