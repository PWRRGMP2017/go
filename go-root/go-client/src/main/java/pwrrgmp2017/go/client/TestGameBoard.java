package pwrrgmp2017.go.client;

import static org.mockito.Mockito.*;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pwrrgmp2017.go.client.gameboard.GameBoardController;
import pwrrgmp2017.go.game.factory.GameInfo;

/**
 * Loads just a Game Board window without any connection. Useful for testing.
 */
public class TestGameBoard extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException
	{
		FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("gameboard/GameBoard.fxml"));

		Parent root = null;
		try
		{
			root = loader.load();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}

		GameBoardController controller = loader.<GameBoardController>getController();
		GameInfo gameInfo = new GameInfo(19, 6.5f, GameInfo.RulesType.JAPANESE, false);

		ServerConnection serverConnection = mock(ServerConnection.class);
		controller.initData(serverConnection, gameInfo, "Test1", "Test2", true);

		Scene scene = new Scene(root);

		primaryStage.setTitle("Go Client");
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.setResizable(true);
		primaryStage.show();
	}
}
