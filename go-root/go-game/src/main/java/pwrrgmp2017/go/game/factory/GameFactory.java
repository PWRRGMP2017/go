package pwrrgmp2017.go.game.factory;

import pwrrgmp2017.go.game.BotGameController;
import pwrrgmp2017.go.game.GameController;
import pwrrgmp2017.go.game.Model.ChineseGameModel;
import pwrrgmp2017.go.game.Model.GameBoard;
import pwrrgmp2017.go.game.Model.GameModel;
import pwrrgmp2017.go.game.Model.JapanGameModel;

public class GameFactory
{

	private volatile static GameFactory INSTANCE;

	private GameFactory()
	{
	}

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
