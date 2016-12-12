package pwrrgmp2017.go.server;

import static org.junit.Assert.*;

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
		server = new Server(0);
		assertTrue(server.getPort() != 0);
		player1 = new RealPlayerConnection(new Socket("localhost", server.getPort()));
		player2 = new RealPlayerConnection(new Socket("localhost", server.getPort()));
		player3 = new RealPlayerConnection(new Socket("localhost", server.getPort()));
		player4 = new RealPlayerConnection(new Socket("localhost", server.getPort()));
		player5 = new RealPlayerConnection(new Socket("localhost", server.getPort()));
		player6 = new RealPlayerConnection(new Socket("localhost", server.getPort()));
	}

	@After
	public void tearDown() throws Exception
	{
		server.close();
	}

	@Test
	public void testIsPuttingUniqueNames()
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
