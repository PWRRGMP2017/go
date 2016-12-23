package pwrrgmp2017.go.server.connection;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pwrrgmp2017.go.clientserverprotocol.InvitationProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.InvitationResponseProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.LoginProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.LoginResponseProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ProtocolMessage;
import pwrrgmp2017.go.game.factory.GameInfo;
import pwrrgmp2017.go.game.factory.GameInfo.RulesType;
import pwrrgmp2017.go.server.GamesManager;

public class NotYetPlayingPlayerHandlerTest
{
	private FakeRealPlayerConnection player1;
	private FakeRealPlayerConnection player2;
	private FakeRealPlayerConnection player3;
	private GamesManager manager;
	
	@Before
	public void setUp() throws IOException
	{
		manager = GamesManager.getInstance();
		
		// Login 3 players the rest of the tests will work with
		player1 = new FakeRealPlayerConnection();
		player2 = new FakeRealPlayerConnection();
		player3 = new FakeRealPlayerConnection();
		
		player1.sendMessageFromFakePlayer(new LoginProtocolMessage("player1").getFullMessage());
		player2.sendMessageFromFakePlayer(new LoginProtocolMessage("player2").getFullMessage());
		player3.sendMessageFromFakePlayer(new LoginProtocolMessage("player3").getFullMessage());
		
		manager.createPlayerConnection(player1);
		manager.createPlayerConnection(player2);
		manager.createPlayerConnection(player3);
		
		LoginResponseProtocolMessage response1 = (LoginResponseProtocolMessage)
				ProtocolMessage.getProtocolMessage(player1.receiveMessageAsFakePlayer());
		LoginResponseProtocolMessage response2 = (LoginResponseProtocolMessage)
				ProtocolMessage.getProtocolMessage(player2.receiveMessageAsFakePlayer());
		LoginResponseProtocolMessage response3 = (LoginResponseProtocolMessage)
				ProtocolMessage.getProtocolMessage(player3.receiveMessageAsFakePlayer());
		
		assertTrue(response1.getIsAccepted());
		assertTrue(response2.getIsAccepted());
		assertTrue(response3.getIsAccepted());
	}
	
	@Test
	public void testInvitationProcess() throws IOException, InterruptedException
	{
		// player1 invites player2
		GameInfo gameInfo = new GameInfo(19, 6.5f, RulesType.JAPANESE, false);
		InvitationProtocolMessage invitation = new InvitationProtocolMessage(
				player1.getPlayerName(), player2.getPlayerName(), gameInfo);
		
		player1.sendMessageFromFakePlayer(invitation.getFullMessage());
		
		// player2 gets the invitation
		InvitationProtocolMessage invitation2 = (InvitationProtocolMessage)
				ProtocolMessage.getProtocolMessage(player2.receiveMessageAsFakePlayer());
		
		assertEquals(invitation2.getFromPlayerName(), invitation.getFromPlayerName());
		assertEquals(invitation2.getToPlayerName(), invitation.getToPlayerName());
		assertEquals(invitation2.getGameInfo(), invitation.getGameInfo());
		
		// player3 should not be able to invite player2 now
		invitation = new InvitationProtocolMessage(player3.getPlayerName(), player2.getPlayerName(), gameInfo);
		player3.sendMessageFromFakePlayer(invitation.getFullMessage());
		InvitationResponseProtocolMessage response = (InvitationResponseProtocolMessage)
				ProtocolMessage.getProtocolMessage(player3.receiveMessageAsFakePlayer());
		assertFalse(response.getIsAccepted());
		
		// player1 cancels the invitation before player2 reacts
		response = new InvitationResponseProtocolMessage(false, "cancelled");
		player1.sendMessageFromFakePlayer(response.getFullMessage());
		
		// player2 reacts but he should receive a response that they are not playing anyway
		player2.sendMessageFromFakePlayer(new InvitationResponseProtocolMessage(true, "I want to play a game").getFullMessage());
		response = (InvitationResponseProtocolMessage)
				ProtocolMessage.getProtocolMessage(player2.receiveMessageAsFakePlayer());
		assertFalse(response.getIsAccepted());
		
		// player3 should now be able to invite player2
		player3.sendMessageFromFakePlayer(invitation.getFullMessage());
		
		invitation2 = (InvitationProtocolMessage)
				ProtocolMessage.getProtocolMessage(player2.receiveMessageAsFakePlayer());
		assertEquals(invitation2.getFromPlayerName(), invitation.getFromPlayerName());
		assertEquals(invitation2.getToPlayerName(), invitation.getToPlayerName());
		assertEquals(invitation2.getGameInfo(), invitation.getGameInfo());
		
		// player2 accepts the invitation
		player2.sendMessageFromFakePlayer(new InvitationResponseProtocolMessage(true, "I want to play a game").getFullMessage());
		
		// player3 gets a response
		response = (InvitationResponseProtocolMessage)
				ProtocolMessage.getProtocolMessage(player3.receiveMessageAsFakePlayer());
		assertTrue(response.getIsAccepted());
		
		System.out.println("here?");
		
		// player3 sends back to server that he's playing
		player3.sendMessageFromFakePlayer(response.getFullMessage());
		
		// player2 gets a response
		response = (InvitationResponseProtocolMessage)
				ProtocolMessage.getProtocolMessage(player2.receiveMessageAsFakePlayer());
		assertTrue(response.getIsAccepted());
		
		manager.closeAllConnections();
	}
	
	@After
	public void tearDown()
	{
		manager.closeAllConnections();
	}
}
