package pwrrgmp2017.go.client;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Runs the client
 */
public class ClientMain extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException
	{
		Parent root = FXMLLoader.load(getClass().getResource("login/login.fxml"));

		Scene scene = new Scene(root);

		primaryStage.setTitle("Go Client");
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	public static Parent moveToScene(Stage stage, String fxml)
	{
		Parent newRoot = null;
		try
		{
			newRoot = FXMLLoader.load(ClientMain.class.getResource(fxml));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}

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
