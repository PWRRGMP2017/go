package pwrrgmp2017.go.server;

import static org.junit.Assert.assertEquals;

import java.net.Socket;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pwrrgmp2017.go.server.Exceptions.SameNameException;

public class GamesManagerTest
{
	GamesManager manager;
	PlayerConnection player1;
	PlayerConnection player2;
	PlayerConnection player3;
	PlayerConnection player4;
	PlayerConnection player5;
	PlayerConnection player6;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
		manager=new GamesManager();
		Socket s = new Socket();
		player1=new RealPlayerConnection(new Socket());
		player2=new RealPlayerConnection(new Socket());
		player3=new RealPlayerConnection(new Socket());
		player4=new RealPlayerConnection(new Socket());
		player5=new RealPlayerConnection(new Socket());
		player6=new RealPlayerConnection(new Socket());
		
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testIsPuttingUniqueNames()
	{
		int i=0;
		try
		{
			manager.addChoosingPlayer(player1, "player1");
			manager.addChoosingPlayer(player2, "player2");
			manager.addChoosingPlayer(player3, "player3");
			manager.addChoosingPlayer(player4, "player1");
			manager.addChoosingPlayer(player5, "player2");
			manager.addChoosingPlayer(player6, "player2");
		}
		catch (SameNameException e)
		{
			e.printStackTrace();
			i++;
		}
		assertEquals(3, i);
	}

}
