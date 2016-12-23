package pwrrgmp2017.go.server;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pwrrgmp2017.go.clientserverprotocol.LoginProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.LoginResponseProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.UnknownProtocolMessage;
import pwrrgmp2017.go.server.Exceptions.SameNameException;
import pwrrgmp2017.go.server.connection.FakeRealPlayerConnection;

public class GamesManagerTest
{
	GamesManager manager;

	@Before
	public void setUp()
	{
		manager = GamesManager.getInstance();
	}

	@After
	public void tearDown()
	{
		manager.closeAllConnections();
	}

	@Test
	public void testIsPuttingUniqueNames() throws IOException, InterruptedException
	{
		int i = 0;
		try
		{
			manager.addChoosingPlayer(new FakeRealPlayerConnection(), "player1");
		}
		catch (SameNameException e)
		{ i++; }
		try
		{
			manager.addChoosingPlayer(new FakeRealPlayerConnection(), "player2");
		}
		catch (SameNameException e)
		{ i++; }
		try
		{
			manager.addChoosingPlayer(new FakeRealPlayerConnection(), "player3");
		}
		catch (SameNameException e)
		{ i++; }
		try
		{
			manager.addChoosingPlayer(new FakeRealPlayerConnection(), "player1");
		}
		catch (SameNameException e)
		{ i++; }
		try
		{
			manager.addChoosingPlayer(new FakeRealPlayerConnection(), "player2");
		}
		catch (SameNameException e)
		{ i++; }
		try
		{
			manager.addChoosingPlayer(new FakeRealPlayerConnection(), "player2");
		}
		catch (SameNameException e)
		{ i++; }
		
		assertEquals(3, i);
		
		manager.closeAllConnections();
	}
	
	@Test
	public void testCreatingNewPlayers() throws IOException
	{
		// Login 3 players, the third should receive a fail message
		FakeRealPlayerConnection player1 = new FakeRealPlayerConnection();
		FakeRealPlayerConnection player2 = new FakeRealPlayerConnection();
		FakeRealPlayerConnection player3 = new FakeRealPlayerConnection();
		
		player1.sendMessageFromFakePlayer(new LoginProtocolMessage("player1").getFullMessage());
		player2.sendMessageFromFakePlayer(new LoginProtocolMessage("player2").getFullMessage());
		
		manager.createPlayerConnection(player1);
		manager.createPlayerConnection(player2);
		
		LoginResponseProtocolMessage response1 = (LoginResponseProtocolMessage)
				ProtocolMessage.getProtocolMessage(player1.receiveMessageAsFakePlayer());
		LoginResponseProtocolMessage response2 = (LoginResponseProtocolMessage)
				ProtocolMessage.getProtocolMessage(player2.receiveMessageAsFakePlayer());
		
		assertTrue(response1.getIsAccepted());
		assertTrue(response2.getIsAccepted());
		
		player3.sendMessageFromFakePlayer(new LoginProtocolMessage("player2").getFullMessage());
		manager.createPlayerConnection(player3);
		LoginResponseProtocolMessage response3 = (LoginResponseProtocolMessage)
				ProtocolMessage.getProtocolMessage(player3.receiveMessageAsFakePlayer());
		assertFalse(response3.getIsAccepted());
		
		// If we sent a wrong message, we should not be logged in too
		player3.resetConnectionState();
		player3.sendMessageFromFakePlayer(new UnknownProtocolMessage("wrong message").getFullMessage());
		manager.createPlayerConnection(player3);
		response3 = (LoginResponseProtocolMessage)
				ProtocolMessage.getProtocolMessage(player3.receiveMessageAsFakePlayer());
		assertFalse(response3.getIsAccepted());
		
		manager.closeAllConnections();
	}

}
