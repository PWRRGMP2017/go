package pwrrgmp2017.go.server;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pwrrgmp2017.go.server.Exceptions.SameNameException;
import pwrrgmp2017.go.server.connection.RealPlayerConnection;

public class GamesManagerTest
{
	GamesManager manager;
	RealPlayerConnection player1;
	RealPlayerConnection player2;
	RealPlayerConnection player3;
	RealPlayerConnection player4;
	RealPlayerConnection player5;
	RealPlayerConnection player6;
	Server server;

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
		manager = new GamesManager();
		player1 = mock(RealPlayerConnection.class);
		player2 = mock(RealPlayerConnection.class);
		player3 = mock(RealPlayerConnection.class);
		player4 = mock(RealPlayerConnection.class);
		player5 = mock(RealPlayerConnection.class);
		player6 = mock(RealPlayerConnection.class);
	//	when(player1.getPlayerName()).thenReturn("Player1");
	//	when(player2.getPlayerName()).thenReturn("Player2");
	//	when(player3.getPlayerName()).thenReturn("Player3");
	//	when(player4.getPlayerName()).thenReturn("Player4");
	//	when(player5.getPlayerName()).thenReturn("Player5");
	//	when(player6.getPlayerName()).thenReturn("Player6");
	}

	@After
	public void tearDown() throws Exception
	{
		server.close();
	}

	@Test
	public void testIsPuttingUniqueNames() throws InterruptedException
	{
		int i = 0;
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
			i++;
		}
		assertEquals(3, i);
	}

}
