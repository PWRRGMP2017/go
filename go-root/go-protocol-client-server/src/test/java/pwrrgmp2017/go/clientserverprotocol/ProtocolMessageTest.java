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

}
