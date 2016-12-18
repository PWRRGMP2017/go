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
		if(INSTANCE==null)
		{
			try
			{
				synchronized(GameFactory.class)
				{
					if(INSTANCE==null)
					{
						INSTANCE= new GameFactory();
					}
				}
			}
			catch(RuntimeException e)
			{
				System.out.println(e.getMessage());
				INSTANCE= null;
			}
		}
		return INSTANCE;
	}

	public GameController createGame(String GameInfo)
	{
		String param=GameInfo.substring(6, 8);  //wielkośc planszy
		GameBoard board=new GameBoard(Integer.parseUnsignedInt(param));
		
		param=GameInfo.substring(12, 15);  //wielkoś komi
		float komi=Float.parseFloat(param);
		
		GameModel model;
		switch(GameInfo.substring(0, 4))  // Zasady gry
		{
		case "JAPAN":
			model= new JapanGameModel(board, komi);
			break;
		case "CHINE":
			model= new ChineseGameModel(board, komi);
			break;
		default:
			model= new JapanGameModel(board, komi);
		}
		
		GameController controller;
		switch(GameInfo.charAt(10))  // gra z/bez bota
		{
		case 'M':
		case 'P':
			controller= new GameController(model);
			break;
		case 'S':
		case 'B':
			controller= new BotGameController(model);
			break;
		default:
			controller= new GameController(model);
		}
		
		return controller;
	}

}
