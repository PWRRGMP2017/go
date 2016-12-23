package pwrrgmp2017.go.server.connection;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import pwrrgmp2017.go.clientserverprotocol.AcceptTerritoryProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ChangeTerritoryProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ConfirmationProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.LoginProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.LoginResponseProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.MoveProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.PassProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.PlayerFoundProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.ProtocolMessage;
import pwrrgmp2017.go.clientserverprotocol.WaitForGameProtocolMessage;
import pwrrgmp2017.go.game.factory.GameInfo;
import pwrrgmp2017.go.game.factory.GameInfo.RulesType;
import pwrrgmp2017.go.server.GamesManager;

public class PlayingPlayerTest
{
	@Test
	public void testPlayingTheGame() throws IOException, InterruptedException
	{
		GamesManager manager = GamesManager.getInstance();
		
		// Login 3 players the rest of the tests will work with
		FakeRealPlayerConnection player1 = new FakeRealPlayerConnection();
		FakeRealPlayerConnection player2 = new FakeRealPlayerConnection();
		
		player1.sendMessageFromFakePlayer(new LoginProtocolMessage("player1").getFullMessage());
		player2.sendMessageFromFakePlayer(new LoginProtocolMessage("player2").getFullMessage());
		
		manager.createPlayerConnection(player1);
		manager.createPlayerConnection(player2);
		
		LoginResponseProtocolMessage loginResponse1 = (LoginResponseProtocolMessage)
				ProtocolMessage.getProtocolMessage(player1.receiveMessageAsFakePlayer());
		LoginResponseProtocolMessage loginResponse2 = (LoginResponseProtocolMessage)
				ProtocolMessage.getProtocolMessage(player2.receiveMessageAsFakePlayer());
		
		assertTrue(loginResponse1.getIsAccepted());
		assertTrue(loginResponse2.getIsAccepted());
		
		// Pair them
		GameInfo gameInfo = new GameInfo(19, 6.5f, RulesType.JAPANESE, false);
		player2.sendMessageFromFakePlayer(new WaitForGameProtocolMessage(gameInfo).getFullMessage());
		
		Thread.sleep(500);
		
		player1.sendMessageFromFakePlayer(new WaitForGameProtocolMessage(gameInfo).getFullMessage());
		
		ProtocolMessage response1 = ProtocolMessage.getProtocolMessage(player1.receiveMessageAsFakePlayer());
		ProtocolMessage response2 = ProtocolMessage.getProtocolMessage(player2.receiveMessageAsFakePlayer());
		
		assertTrue(response1 instanceof PlayerFoundProtocolMessage);
		assertTrue(response2 instanceof PlayerFoundProtocolMessage);
		
		player1.sendMessageFromFakePlayer(new ConfirmationProtocolMessage().getFullMessage());
		player2.sendMessageFromFakePlayer(new ConfirmationProtocolMessage().getFullMessage());
		
		// Play a little
		player1.sendMessageFromFakePlayer(new MoveProtocolMessage(1, 1).getFullMessage());
		response2 = ProtocolMessage.getProtocolMessage(player2.receiveMessageAsFakePlayer());
		
		player2.sendMessageFromFakePlayer(new MoveProtocolMessage(2, 2).getFullMessage());
		response1 = ProtocolMessage.getProtocolMessage(player1.receiveMessageAsFakePlayer());
		
		player1.sendMessageFromFakePlayer(new PassProtocolMessage().getFullMessage());
		response2 = ProtocolMessage.getProtocolMessage(player2.receiveMessageAsFakePlayer());
		
		player2.sendMessageFromFakePlayer(new PassProtocolMessage().getFullMessage());
		response1 = ProtocolMessage.getProtocolMessage(player1.receiveMessageAsFakePlayer());
		
		player1.sendMessageFromFakePlayer(new ChangeTerritoryProtocolMessage(1, 2).getFullMessage());
		response2 = ProtocolMessage.getProtocolMessage(player2.receiveMessageAsFakePlayer());
		
		player1.sendMessageFromFakePlayer(new AcceptTerritoryProtocolMessage().getFullMessage());
		response2 = ProtocolMessage.getProtocolMessage(player2.receiveMessageAsFakePlayer());
		
		player2.sendMessageFromFakePlayer(new AcceptTerritoryProtocolMessage().getFullMessage());
		response1 = ProtocolMessage.getProtocolMessage(player1.receiveMessageAsFakePlayer());
		
		Thread.sleep(500);
		assertTrue(player1.getPlayerInfo().getPlayingGame() == null);
		assertTrue(player2.getPlayerInfo().getPlayingGame() == null);
		
		manager.closeAllConnections();
	}
}
