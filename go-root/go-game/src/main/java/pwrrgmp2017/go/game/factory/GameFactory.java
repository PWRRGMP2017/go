package pwrrgmp2017.go.game.factory;

import pwrrgmp2017.go.game.BotGameController;
import pwrrgmp2017.go.game.GameController;
import pwrrgmp2017.go.game.Model.ChineseGameModel;
import pwrrgmp2017.go.game.Model.GameBoard;
import pwrrgmp2017.go.game.Model.GameModel;
import pwrrgmp2017.go.game.Model.JapanGameModel;

/**
 * Class which is pure fabrication. SINGLETON, SIMPLE FACTORY
 * It's a factory of games wrapped in GameController
 * @author Robert Gawlik
 *
 */
public class GameFactory
{
	/**Instance of singleton */
	private volatile static GameFactory INSTANCE;

	/**
	 * Constructor of the class
	 */
	private GameFactory()
	{
	}

	/**
	 * Multi-threading method which gets instance of this factory
	 * @return
	 */
	public static GameFactory getInstance()
	{
		if (INSTANCE == null)
		{
			try
			{
				synchronized (GameFactory.class)
				{
					if (INSTANCE == null)
					{
						INSTANCE = new GameFactory();
					}
				}
			}
			catch (RuntimeException e)
			{
				System.out.println(e.getMessage());
				INSTANCE = null;
			}
		}
		return INSTANCE;
	}

	/**
	 * Method which creates GO game
	 * @param gameInfoAsString informations of game which must be created
	 * @return Controller with current GameModel
	 * @throws IllegalArgumentException bad info about game
	 */
	public GameController createGame(String gameInfoAsString) throws IllegalArgumentException
	{
		GameInfo gameInfo = new GameInfo(gameInfoAsString);

		GameBoard board = new GameBoard(gameInfo.getBoardSize());
		float komi = gameInfo.getKomiValue();

		GameModel model;
		switch (gameInfo.getRulesType()) // Zasady gry
		{
		case JAPANESE:
			model = new JapanGameModel(board, komi);
			break;

		case CHINESE:
			model = new ChineseGameModel(board, komi);
			break;

		default:
			model = new JapanGameModel(board, komi);
		}

		GameController controller;
		if (!gameInfo.getIsBot())
		{
			controller = new GameController(model);
		}
		else
		{
			controller = new BotGameController(model);
		}

		return controller;
	}

}
