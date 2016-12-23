package pwrrgmp2017.go.client;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Runs the client JavaFX application.
 */
public class ClientMain extends Application
{
	/**
	 * Launches the JavaFX application.
	 * @param args command line parameters
	 */
	public static void main(String[] args)
	{
		launch(args);
	}

	/**
	 * Loads the Login window and shows it.
	 */
	@Override
	public void start(Stage primaryStage) throws IOException
	{
		Parent root = FXMLLoader.load(getClass().getResource("login/Login.fxml"));

		Scene scene = new Scene(root);

		primaryStage.setTitle("Go Client");
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	/**
	 * Changes the scene of the window (stage) provided in an FXML file in the given path.
	 * @param stage window to be changed
	 * @param fxml relative path to the FXML file of the new scene
	 * @return new root
	 */
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
