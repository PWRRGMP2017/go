package pwrrgmp2017.go.client.gameboard;

import java.awt.Point;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

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
import pwrrgmp2017.go.clientserverprotocol.AcceptTerritoryProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ChangeTerritoryProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.MoveProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.PassProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ResignProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ResumeGameProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.UnknownProtocolMessage;
import pwrrgmp2017.go.game.GameController;
import pwrrgmp2017.go.game.Exception.GameBegginsException;
import pwrrgmp2017.go.game.Exception.GameIsEndedException;
import pwrrgmp2017.go.game.Exception.GameStillInProgressException;
import pwrrgmp2017.go.game.Exceptions.BadFieldException;
import pwrrgmp2017.go.game.GameStates.GameStateEnum;
import pwrrgmp2017.go.game.Model.GameBoard;
import pwrrgmp2017.go.game.Model.GameBoard.Field;
import pwrrgmp2017.go.game.factory.GameFactory;
import pwrrgmp2017.go.game.factory.GameInfo;

public class GameBoardController implements Observer
{
	protected static final Logger LOGGER = Logger.getLogger(GameBoardController.class.getName());

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
	
	@FXML
	private Button resumeButton;

	private GameInfo gameInfo;
	private ServerConnection serverConnection;
	private String thisPlayerName;
	private GameBoard.Field playerColor;
	private String thisIdPrefix;
	private String opponentIdPrefix;
	private GameController gameController;
	private Button[][] boardPaneButtons;
	private Field[][] territoryBoard;
	private boolean acceptedPreviousTurn;
	private boolean isGameWithBot;

	public void initData(ServerConnection serverConnection, GameInfo gameInfo, String blackPlayerName,
			String whitePlayerName, boolean isThisPlayerBlack)
	{
		this.serverConnection = serverConnection;
		serverConnection.addObserver(this);
		isGameWithBot = gameInfo.getIsBot();
		gameInfo = new GameInfo(gameInfo.getBoardSize(), gameInfo.getKomiValue(), gameInfo.getRulesType(), false);
		this.gameInfo = gameInfo;
		playerColor = isThisPlayerBlack ? Field.BLACKSTONE : Field.WHITESTONE;
		thisIdPrefix = isThisPlayerBlack ? "black-" : "white-";
		opponentIdPrefix = !isThisPlayerBlack ? "black-" : "white-";
		thisPlayerName = isThisPlayerBlack ? blackPlayerName : whitePlayerName;
		blackPlayerLabel.setText(blackPlayerName);
		whitePlayerLabel.setText(whitePlayerName);
		passButton.setDisable(true);
		acceptButton.setDisable(true);

		generateBoardPane();
		
		

		gameController = GameFactory.getInstance().createGame(gameInfo.getAsString());
		try
		{
			gameController.initialiseGame(Field.BLACKSTONE);
		}
		catch (GameStillInProgressException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		catch (BadFieldException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		updateBoardPane();
		setStats();
		passButton.setDisable(false);
	}
	
	private void setStats()
	{
		stateLabel.setText(gameController.getState().toString());
		
		StringBuilder stats = new StringBuilder();
		
		stats.append("Turn: 0\n"); // Not yet implemented
		Point lastMove = gameController.getLastMovement();
		stats.append("Last move: (" + lastMove.x + ", " + lastMove.y + ")\n");
		
		if (gameController.getState() == GameStateEnum.END)
		{
			float score = gameController.calculateScore(territoryBoard);
			String winner;
			if (score < 0)
			{
				winner = "white";
				score *= -1;
			}
			else if (score > 0)
			{
				winner = "black";
			}
			else
			{
				winner = "nobody";
			}
			stats.append("Score: " + winner + " wins by " + score + " points\n");
		}
		
		stats.append("\n");
		
		stats.append("Game settings\n");
		stats.append("Game rules: " + gameInfo.getRulesType().toString() + "\n");
		stats.append("Board size: " + boardPaneButtons.length + "x" + boardPaneButtons.length + "\n");
		stats.append("Komi: " + gameController.getKomi() + "\n");
		stats.append("Bot: " + (gameInfo.getIsBot() ? "Yes" : "No") + "\n");
		stats.append("\n");
		
		stats.append("Black player captives: " + gameController.getWhiteCaptives() + "\n");
		stats.append("White player captives: " + gameController.getBlackCaptives() + "\n");
		
		statsTextArea.setText(stats.toString());
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
	}

	private String getThisStyleId(String postfix)
	{
		return thisIdPrefix + postfix;
	}

	private String getOpponentStyleId(String postfix)
	{
		return opponentIdPrefix + postfix;
	}
	
	private String getStyleId(Field colour, String postfix)
	{
		if (colour == this.playerColor)
		{
			return getThisStyleId(postfix);
		}
		else
		{
			return getOpponentStyleId(postfix);
		}
	}
	
	private Field getOpponentColor()
	{
		return playerColor == Field.BLACKSTONE ? Field.WHITESTONE : Field.BLACKSTONE;
	}

	private boolean isOurTurn()
	{
		if (playerColor == Field.BLACKSTONE)
		{
			return gameController.getState() == GameStateEnum.BLACKMOVE;
		}
		else
		{
			return gameController.getState() == GameStateEnum.WHITEMOVE;
		}
	}
	
	private void updateBoardPane()
	{
		Field[][] board = gameController.getBoardCopy();
		boolean isOurTurn = isOurTurn();
		boolean[][] possibleMovements;
		try
		{
			possibleMovements = gameController.getPossibleMovements(playerColor);
		}
		catch (BadFieldException e)
		{
			// Should not happen
			e.printStackTrace();
			System.exit(1);
			return; // Because Eclipse shows an error
		}
		
		for (int i = 0; i < boardPaneButtons.length; ++i)
		{
			for (int j = 0; j < boardPaneButtons[i].length; ++j)
			{
				Button button = boardPaneButtons[i][j];
				Field field = board[i+1][j+1];
				boolean isMovementPossible = possibleMovements[i][j];
				
				switch (field)
				{
				case EMPTY:
					button.setId(getThisStyleId("empty"));
					break;
					
				case BLACKSTONE:
					button.setId(getStyleId(Field.BLACKSTONE, "stone"));
					break;
				
				case WHITESTONE:
					button.setId(getStyleId(Field.WHITESTONE, "stone"));
					break;
					
				default:
					LOGGER.warning("Unexpected field: " + field);
				}
				
				if (isOurTurn)
					button.setDisable(!isMovementPossible);
				else
					button.setDisable(true);
			}
		}
	}
	
	private void changeTerritory(int x, int y)
	{
		Field field = territoryBoard[x][y];
		switch (field)
		{
		case NONETERRITORY:
			territoryBoard[x][y] = Field.BLACKTERRITORY;
			break;
			
		case BLACKTERRITORY:
			territoryBoard[x][y] = Field.WHITETERRITORY;
			break;
			
		case WHITETERRITORY:
			territoryBoard[x][y] = Field.NONETERRITORY;
			break;
			
		case WHITESTONE:
			territoryBoard[x][y] = Field.DEADWHITE;
			break;
		
		case DEADWHITE:
			territoryBoard[x][y] = Field.WHITESTONE;
			break;
		
		case BLACKSTONE:
			territoryBoard[x][y] = Field.DEADBLACK;
			break;
		
		case DEADBLACK:
			territoryBoard[x][y] = Field.BLACKSTONE;
			break;
		
		default:
			LOGGER.warning("Unexpected field: " + field.toString());
		}
	}
	
	private void updateBoardPaneUsingTerritories(boolean isOurTurn)
	{
		if (territoryBoard == null)
		{
			LOGGER.warning("territoryBoard is null");
			return;
		}
		
		for (int i = 0; i < boardPaneButtons.length; ++i)
		{
			for (int j = 0; j < boardPaneButtons[i].length; ++j)
			{
				Button button = boardPaneButtons[i][j];
				Field field = territoryBoard[i+1][j+1];
				
				switch (field)
				{
				case NONETERRITORY:
					button.setId(getThisStyleId("nonterritory"));
					break;
					
				case BLACKTERRITORY:
					button.setId(getStyleId(Field.BLACKSTONE, "territory"));
					break;
					
				case WHITETERRITORY:
					button.setId(getStyleId(Field.WHITESTONE, "territory"));
					break;
					
				case WHITESTONE:
					button.setId(getStyleId(Field.WHITESTONE, "stone"));
					break;
				
				case DEADWHITE:
					button.setId(getStyleId(Field.WHITESTONE, "stone-dead"));
					break;
				
				case BLACKSTONE:
					button.setId(getStyleId(Field.BLACKSTONE, "stone"));
					break;
				
				case DEADBLACK:
					button.setId(getStyleId(Field.BLACKSTONE, "stone-dead"));
					break;
				
				default:
					LOGGER.warning("Unexpected field: " + field.toString());
				}

				button.setDisable(isOurTurn);
			}
		}
	}

	private int[] getPositionFromButton(Button button)
	{
		int position[] = { -1, -1 };
		for (int i = 0; i < boardPaneButtons.length; ++i)
		{
			for (int j = 0; j < boardPaneButtons[i].length; ++j)
			{
				if (boardPaneButtons[i][j] == button)
				{
					position[0] = i;
					position[1] = j;
					return position;
				}
			}
		}
		return position;
	}

	@FXML
	public void initialize()
	{

	}

	protected void afterTurn(boolean isOurTurn)
	{
		updateBoardPane();
		setStats();
		passButton.setDisable(isOurTurn);
		acceptButton.setDisable(true);
		resumeButton.setDisable(true);
	}

	protected void afterTerritoryTurn(boolean isOurTurn)
	{
		passButton.setDisable(true);
		acceptButton.setDisable(isOurTurn);
		resumeButton.setDisable(isOurTurn);
		setStats();
		updateBoardPaneUsingTerritories(isOurTurn);
	}

	protected void handleBoardClick(ActionEvent event)
	{
		// Assuming that wrong buttons cannot be pressed
		Button pressedButton = (Button) event.getSource();
		int[] position = getPositionFromButton(pressedButton);
		
		position[0] += 1;
		position[1] += 1;
		
		if (gameController.getState() == GameStateEnum.END)
		{
			acceptedPreviousTurn = false;
			
			changeTerritory(position[0], position[1]);
			
			// Send our movement to the server
			ChangeTerritoryProtocolMessage message = new ChangeTerritoryProtocolMessage(position[0], position[1]);
			serverConnection.send(message.getFullMessage());
			
			afterTerritoryTurn(false);
		}
		else
		{
			// Make movement on our end
			try
			{
				gameController.addMovement(position[0], position[1], playerColor);
			}
			catch (BadFieldException | GameBegginsException | GameIsEndedException e)
			{
				// Should not happen
				e.printStackTrace();
			}
			
			// Send our movement to the server
			MoveProtocolMessage message = new MoveProtocolMessage(position[0], position[1]);
			serverConnection.send(message.getFullMessage());
			
			afterTurn(true);
		}
	}

	@FXML
	protected void handleResign()
	{
		try
		{
			gameController.resign();
		}
		catch (GameIsEndedException e)
		{
			// So what?
//			e.printStackTrace();
		}
		
		// Send the resign message
		ResignProtocolMessage message = new ResignProtocolMessage("Your opponent has resigned.");
		serverConnection.send(message.getFullMessage());

		// Move to GameSettings
		moveToGameSettingsScene();
	}
	
	@FXML
	protected void handlePass()
	{
		try
		{
			gameController.pass(playerColor);
		}
		catch (GameBegginsException | GameIsEndedException | BadFieldException e)
		{
			// Should not happen
			e.printStackTrace();
		}
		
		serverConnection.send(new PassProtocolMessage().getFullMessage());
		
		if (gameController.getState() == GameStateEnum.END)
		{		
			// We are marking territories!
			territoryBoard = gameController.getPossibleTerritory();
			
			if (isGameWithBot)
			{
				showTheWinner();
				return;
			}
			
			acceptedPreviousTurn = false;
			afterTerritoryTurn(true);
		}
		else
		{
			afterTurn(true);
		}
	}
	
	@FXML
	protected void handleAccept()
	{
		serverConnection.send(new AcceptTerritoryProtocolMessage().getFullMessage());
		if (acceptedPreviousTurn)
		{
			showTheWinner();
		}
		acceptedPreviousTurn = true;
		afterTerritoryTurn(true);
	}
	
	@FXML
	protected void handleResume()
	{
		acceptedPreviousTurn = false;
		
		try
		{
			gameController.initialiseGame(getOpponentColor());
		}
		catch (GameStillInProgressException | BadFieldException e)
		{
			// Should not happen
			e.printStackTrace();
			return;
		}
		
		serverConnection.send(new ResumeGameProtocolMessage().getFullMessage());
		territoryBoard = null;
		afterTurn(true);
	}

	private void showTheWinner()
	{
		String result;
		float score = gameController.calculateScore(territoryBoard);
		String winner;
		if (score < 0)
		{
			winner = "White";
			score *= -1;
		}
		else if (score > 0)
		{
			winner = "Black";
		}
		else
		{
			winner = "Nobody";
		}
		result = winner + " wins by " + score + " points.\n";
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText("Result of the game");
		alert.setContentText(result);
		alert.showAndWait();
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
				serverConnection.deleteObserver(this);
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
				try
				{
					gameController.resign();
				}
				catch (GameIsEndedException e)
				{
					// So what?
//					e.printStackTrace();
				}
				
				Platform.runLater(() ->
				{
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setHeaderText("Resignation");
					alert.setContentText(((ResignProtocolMessage) arg).getReason());
					alert.showAndWait();
					moveToGameSettingsScene();
				});
			}
			else if (arg instanceof MoveProtocolMessage)
			{
				// The second player made a move
				MoveProtocolMessage movement = (MoveProtocolMessage) arg;
				
				// Make movement on our end
				try
				{
					gameController.addMovement(movement.getX(), movement.getY(), getOpponentColor());
				}
				catch (BadFieldException | GameBegginsException | GameIsEndedException e)
				{
					// Should not happen
					e.printStackTrace();
				}
				
				Platform.runLater(()->
				{
					afterTurn(false);
				});
			}
			else if (arg instanceof PassProtocolMessage)
			{
				try
				{
					gameController.pass(getOpponentColor());
				}
				catch (GameBegginsException | GameIsEndedException | BadFieldException e)
				{
					// Should not happen
					e.printStackTrace();
				}
				
				if (gameController.getState() == GameStateEnum.END)
				{
					// We are marking territories!
					territoryBoard = gameController.getPossibleTerritory();
					
					if (isGameWithBot)
					{
						Platform.runLater(()->
						{
							showTheWinner();
						});
						return;
					}
					
					acceptedPreviousTurn = false;
					
					Platform.runLater(()->
					{
						afterTerritoryTurn(false);
					});
				}
				else
				{
					Platform.runLater(()->
					{
						afterTurn(false);
					});
				}
			}
			else if (arg instanceof ChangeTerritoryProtocolMessage)
			{
				acceptedPreviousTurn = false;
				ChangeTerritoryProtocolMessage message = (ChangeTerritoryProtocolMessage) arg;
				changeTerritory(message.getX(), message.getY());
				
				Platform.runLater(()->
				{
					afterTerritoryTurn(true);
				});
			}
			else if (arg instanceof AcceptTerritoryProtocolMessage)
			{
				Platform.runLater(()->
				{
					if (acceptedPreviousTurn)
					{
						showTheWinner();
					}
					afterTerritoryTurn(false);
					acceptedPreviousTurn = true;
				});
				
			}
			else if (arg instanceof ResumeGameProtocolMessage)
			{
				acceptedPreviousTurn = false;
				
				try
				{
					gameController.initialiseGame(playerColor);
				}
				catch (GameStillInProgressException | BadFieldException e)
				{
					// Should not happen
					e.printStackTrace();
					return;
				}
				
				territoryBoard = null;
				
				Platform.runLater(()->
				{
					afterTurn(false);
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
