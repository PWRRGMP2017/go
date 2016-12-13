package pwrrgmp2017.go.game.Builder;

import pwrrgmp2017.go.game.BotGameController;
import pwrrgmp2017.go.game.GameController;
import pwrrgmp2017.go.game.Model.ChineseGameModel;
import pwrrgmp2017.go.game.Model.GameBoard;
import pwrrgmp2017.go.game.Model.GameModel;
import pwrrgmp2017.go.game.Model.JapanGameModel;

public class GameBuilderDirector
{
	
	
	private volatile static GameBuilderDirector INSTANCE;
	
	private GameBuilderDirector()
	{
	}
	
	public static GameBuilderDirector getInstance()
	{
		if(INSTANCE==null)
		{
			try
			{
				synchronized(GameBuilderDirector.class)
				{
					if(INSTANCE==null)
					{
						INSTANCE= new GameBuilderDirector();
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
		String param=GameInfo.substring(6, 8);
		GameBoard board=new GameBoard(Integer.parseUnsignedInt(param));
		param=GameInfo.substring(12, 14);
		float komi=Float.parseFloat(param);
		GameModel model;
		switch(GameInfo.substring(0, 4))
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
		switch(GameInfo.charAt(10))
		{
		case 'S':
		case 'P':
		case 'O':
			controller= new GameController(model);
			break;
		case 'M':
		case 'B':
			controller= new BotGameController(model);
			break;
		default:
			controller= new GameController(model);
		}
		
		return controller;
	}

}
