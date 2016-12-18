package pwrrgmp2017.go.game;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pwrrgmp2017.go.game.factory.GameFactory;

public class GameControllerTest
{
	GameFactory factory;
	GameController controller;
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
		factory= GameFactory.getInstance();
		String gameInfo="JAPAN_019_M_06.5";
		controller=factory.createGame(gameInfo);
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void test()
	{
		fail("Not yet implemented");
	}

}
