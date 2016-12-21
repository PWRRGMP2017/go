package pwrrgmp2017.go.clientserverprotocol;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

import pwrrgmp2017.go.game.factory.GameInfo;

public class ProtocolMessageTest
{

	@Test
	public void testProtocolCommandsNotSame()
	{
		HashSet<String> hashSet = new HashSet<>();
		assertTrue(hashSet.add(UnknownProtocolMessage.getCommand()));
		assertTrue(hashSet.add(LoginProtocolMessage.getCommand()));
		assertTrue(hashSet.add(LoginResponseProtocolMessage.getCommand()));
		assertTrue(hashSet.add(ExitProtocolMessage.getCommand()));
		assertTrue(hashSet.add(InvitationProtocolMessage.getCommand()));
		assertTrue(hashSet.add(InvitationResponseProtocolMessage.getCommand()));
		assertTrue(hashSet.add(ResignProtocolMessage.getCommand()));
		assertTrue(hashSet.add(ConfirmationProtocolMessage.getCommand()));
		assertTrue(hashSet.add(MoveProtocolMessage.getCommand()));
		assertTrue(hashSet.add(AcceptTerritoryProtocolMessage.getCommand()));
		assertTrue(hashSet.add(ResumeGameProtocolMessage.getCommand()));
		assertTrue(hashSet.add(PassProtocolMessage.getCommand()));
		assertTrue(hashSet.add(ChangeTerritoryProtocolMessage.getCommand()));
		assertTrue(hashSet.add(PlayBotGameProtocolMessage.getCommand()));
	}

	@Test
	public void testLoginProtocolMessage()
	{
		LoginProtocolMessage sentMessage = new LoginProtocolMessage("User 1234");
		assertEquals("User 1234", sentMessage.getUsername());

		ProtocolMessage receivedMessage = ProtocolMessage.getProtocolMessage(sentMessage.getFullMessage());
		assertEquals(sentMessage.getUsername(), ((LoginProtocolMessage) (receivedMessage)).getUsername());
	}

	@Test
	public void testLoginResponseProtocolMessage()
	{
		LoginResponseProtocolMessage sentMessage = new LoginResponseProtocolMessage(false, "Because yes");
		assertEquals("Because yes", sentMessage.getReason());
		assertEquals(false, sentMessage.getIsAccepted());

		ProtocolMessage receivedMessage = ProtocolMessage.getProtocolMessage(sentMessage.getFullMessage());
		LoginResponseProtocolMessage loginResponseProtocolMessage = (LoginResponseProtocolMessage) receivedMessage;
		assertEquals(sentMessage.getIsAccepted(), loginResponseProtocolMessage.getIsAccepted());
		assertEquals(sentMessage.getReason(), loginResponseProtocolMessage.getReason());
	}

	@Test
	public void testExitProtocolMessage()
	{
		ExitProtocolMessage sentMessage = new ExitProtocolMessage();

		ProtocolMessage receivedMessage = ProtocolMessage.getProtocolMessage(sentMessage.getFullMessage());
		assertEquals(sentMessage.getFullMessage(), receivedMessage.getFullMessage());
	}

	@Test
	public void testWrongMessage()
	{
		ProtocolMessage message = ProtocolMessage.getProtocolMessage("asdasdf;qwerty");
		assertEquals("asdasdf;qwerty", message.getFullMessage());
		assertTrue(message instanceof UnknownProtocolMessage);
	}

	@Test
	public void testInvitationMessage()
	{
		GameInfo gameInfo = new GameInfo(19, 6.5f, GameInfo.RulesType.JAPANESE, false);
		String player1Name = "player1";
		String player2Name = "player2";

		InvitationProtocolMessage sentMessage = new InvitationProtocolMessage(player1Name, player2Name, gameInfo);
		assertEquals(player1Name, sentMessage.getFromPlayerName());
		assertEquals(player2Name, sentMessage.getToPlayerName());
		assertSame(gameInfo, sentMessage.getGameInfo());

		ProtocolMessage message = ProtocolMessage.getProtocolMessage(sentMessage.getFullMessage());
		InvitationProtocolMessage receivedMessage = (InvitationProtocolMessage) message;
		assertEquals(player1Name, receivedMessage.getFromPlayerName());
		assertEquals(player2Name, receivedMessage.getToPlayerName());
		assertEquals(gameInfo, receivedMessage.getGameInfo());
	}

	@Test
	public void testInvitationResponseMessage()
	{
		String reason = "Player is already invited.";
		InvitationResponseProtocolMessage sentMessage = new InvitationResponseProtocolMessage(false, reason);
		assertEquals(false, sentMessage.getIsAccepted());
		assertEquals(reason, sentMessage.getReason());

		ProtocolMessage message = ProtocolMessage.getProtocolMessage(sentMessage.getFullMessage());
		InvitationResponseProtocolMessage receivedMessage = (InvitationResponseProtocolMessage) message;
		assertEquals(false, receivedMessage.getIsAccepted());
		assertEquals(reason, receivedMessage.getReason());
	}

	@Test
	public void testResignProtocolMessage()
	{
		String reason = "Player is already invited.";
		ResignProtocolMessage sentMessage = new ResignProtocolMessage(reason);
		assertEquals(reason, sentMessage.getReason());

		ProtocolMessage message = ProtocolMessage.getProtocolMessage(sentMessage.getFullMessage());
		ResignProtocolMessage receivedMessage = (ResignProtocolMessage) message;
		assertEquals(reason, receivedMessage.getReason());
	}

	@Test
	public void testConfirmationProtocolMessage()
	{
		ConfirmationProtocolMessage sentMessage = new ConfirmationProtocolMessage();
		ProtocolMessage message = ProtocolMessage.getProtocolMessage(sentMessage.getFullMessage());
		assertTrue(message instanceof ConfirmationProtocolMessage);
	}

	@Test
	public void testMoveProtocolMessage()
	{
		int x = 20;
		int y = 15;
		MoveProtocolMessage sentMessage = new MoveProtocolMessage(x, y);
		assertEquals(x, sentMessage.getX());
		assertEquals(y, sentMessage.getY());

		ProtocolMessage message = ProtocolMessage.getProtocolMessage(sentMessage.getFullMessage());
		MoveProtocolMessage receivedMessage = (MoveProtocolMessage) message;
		assertEquals(x, receivedMessage.getX());
		assertEquals(y, receivedMessage.getY());
	}
	
	@Test
	public void testChangeTerritoryProtocolMessage()
	{
		int x = 20;
		int y = 15;
		ChangeTerritoryProtocolMessage sentMessage = new ChangeTerritoryProtocolMessage(x, y);
		assertEquals(x, sentMessage.getX());
		assertEquals(y, sentMessage.getY());

		ProtocolMessage message = ProtocolMessage.getProtocolMessage(sentMessage.getFullMessage());
		ChangeTerritoryProtocolMessage receivedMessage = (ChangeTerritoryProtocolMessage) message;
		assertEquals(x, receivedMessage.getX());
		assertEquals(y, receivedMessage.getY());
	}
	
	@Test
	public void testAcceptTerritoryProtocolMessage()
	{
		AcceptTerritoryProtocolMessage sentMessage = new AcceptTerritoryProtocolMessage();
		ProtocolMessage message = ProtocolMessage.getProtocolMessage(sentMessage.getFullMessage());
		assertTrue(message instanceof AcceptTerritoryProtocolMessage);
	}
	
	@Test
	public void testResumeGameProtocolMessage()
	{
		ResumeGameProtocolMessage sentMessage = new ResumeGameProtocolMessage();
		ProtocolMessage message = ProtocolMessage.getProtocolMessage(sentMessage.getFullMessage());
		assertTrue(message instanceof ResumeGameProtocolMessage);
	}
	
	@Test
	public void testPassProtocolMessage()
	{
		PassProtocolMessage sentMessage = new PassProtocolMessage();
		ProtocolMessage message = ProtocolMessage.getProtocolMessage(sentMessage.getFullMessage());
		assertTrue(message instanceof PassProtocolMessage);
	}
	
	@Test
	public void testPlayBotGameProtocolMessage()
	{
		GameInfo gameInfo = new GameInfo(19, 6.5f, GameInfo.RulesType.JAPANESE, true);
		String playerName = "player";

		PlayBotGameProtocolMessage sentMessage = new PlayBotGameProtocolMessage(playerName, gameInfo);
		assertEquals(playerName, sentMessage.getPlayerName());
		assertSame(gameInfo, sentMessage.getGameInfo());

		ProtocolMessage message = ProtocolMessage.getProtocolMessage(sentMessage.getFullMessage());
		PlayBotGameProtocolMessage receivedMessage = (PlayBotGameProtocolMessage) message;
		assertEquals(playerName, receivedMessage.getPlayerName());
		System.out.println(receivedMessage.getGameInfo().getAsString());
		assertEquals(gameInfo, receivedMessage.getGameInfo());
	}

}
