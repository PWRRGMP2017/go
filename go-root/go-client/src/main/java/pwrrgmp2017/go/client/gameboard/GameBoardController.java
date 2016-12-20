package pwrrgmp2017.go.client.gameboard;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import pwrrgmp2017.go.client.ClientMain;
import pwrrgmp2017.go.client.ServerConnection;
import pwrrgmp2017.go.client.gamesettings.GameSettingsController;
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
	private boolean isThisPlayerBlack;
	private String thisIdPrefix;
	private String opponentIdPrefix;

	private Button[][] boardPaneButtons;

	public void initData(ServerConnection serverConnection, GameInfo gameInfo, String blackPlayerName,
			String whitePlayerName, boolean isThisPlayerBlack)
	{
		this.serverConnection = serverConnection;
		serverConnection.addObserver(this);
		this.gameInfo = gameInfo;

		// TODO create GameController here

		// TODO generate board pane here

		this.isThisPlayerBlack = isThisPlayerBlack;
		thisIdPrefix = isThisPlayerBlack ? "black-" : "white-";
		opponentIdPrefix = !isThisPlayerBlack ? "black-" : "white-";
		thisPlayerName = isThisPlayerBlack ? blackPlayerName : whitePlayerName;
		blackPlayerLabel.setText(blackPlayerName);
		whitePlayerLabel.setText(whitePlayerName);
		passButton.setDisable(true);
		acceptButton.setDisable(true);

		generateBoardPane();
	}

	private void generateBoardPane()
	{
		int boardSize = gameInfo.getBoardSize();
		boardPaneButtons = new Button[boardSize][boardSize];

		// Let children take the whole cell
		boardPane.getRowConstraints().clear();
		boardPane.getColumnConstraints().clear();
		for (int rowIndex = 0; rowIndex < boardSize; rowIndex++)
		{
			RowConstraints rc = new RowConstraints();
			rc.setVgrow(Priority.ALWAYS); // allow row to grow
			rc.setFillHeight(true); // ask nodes to fill height for row
			boardPane.getRowConstraints().add(rc);
		}
		for (int colIndex = 0; colIndex < boardSize; colIndex++)
		{
			ColumnConstraints cc = new ColumnConstraints();
			cc.setHgrow(Priority.ALWAYS); // allow column to grow
			cc.setFillWidth(true); // ask nodes to fill space for column
			boardPane.getColumnConstraints().add(cc);
		}

		for (int column = 0; column < boardSize; ++column)
		{
			for (int row = 0; row < boardSize; ++row)
			{
				// Add an intersection
				Label cross = new Label("");
				String id;
				if (row == 0)
				{
					if (column == 0)
					{
						id = "cross-top-left";
					}
					else if (column < boardSize - 1)
					{
						id = "cross-top-center";
					}
					else
					{
						id = "cross-top-right";
					}
				}
				else if (row < boardSize - 1)
				{
					if (column == 0)
					{
						id = "cross-left-center";
					}
					else if (column < boardSize - 1)
					{
						id = "cross-center";
					}
					else
					{
						id = "cross-right-center";
					}
				}
				else
				{
					if (column == 0)
					{
						id = "cross-bottom-left";
					}
					else if (column < boardSize - 1)
					{
						id = "cross-bottom-center";
					}
					else
					{
						id = "cross-bottom-right";
					}
				}
				cross.setId(id);
				cross.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
				boardPane.add(cross, column, row);

				// Add a button on top of the intersection
				boardPaneButtons[row][column] = new Button();
				Button button = boardPaneButtons[row][column];
				button.setId(getThisStyleId("empty"));
				button.setMinSize(1, 1);
				button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
				button.setOnAction(this::handleBoardClick);
				boardPane.add(button, column, row);
			}
		}

		// Just for test
		boardPaneButtons[0][0].setId(getThisStyleId("territory"));
		boardPaneButtons[1][0].setId(getThisStyleId("stone-dead"));
	}

	private String getThisStyleId(String postfix)
	{
		return thisIdPrefix + postfix;
	}

	private String getOpponentStyleId(String postfix)
	{
		return opponentIdPrefix + postfix;
	}

	protected void handleBoardClick(ActionEvent event)
	{
		Button pressedButton = (Button) event.getSource();
		pressedButton.setId(getThisStyleId("stone"));
		pressedButton.setDisable(true);
		// TODO make proper changes according to the gamecontroller
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
