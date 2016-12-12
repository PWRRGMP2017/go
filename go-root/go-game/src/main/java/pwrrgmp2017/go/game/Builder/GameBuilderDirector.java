package pwrrgmp2017.go.game.Builder;

import pwrrgmp2017.go.game.GameController;

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

	public GameController createGame(String gameInfo)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
