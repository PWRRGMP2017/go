package pwrrgmp2017.go.clientserverprotocol;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

public class ProtocolMessageTest
{

	@Test
	public void testProtocolCommandsNotSame()
	{
		HashSet<String> hashSet = new HashSet<>();
		assertTrue(hashSet.add(LoginProtocolMessage.getCommand()));
		assertTrue(hashSet.add(LoginResponseProtocolMessage.getCommand()));
		assertTrue(hashSet.add(ExitProtocolMessage.getCommand()));
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
		ExitProtocolMessage exitProtocolMessage = (ExitProtocolMessage) receivedMessage;
		assertEquals(sentMessage.getFullMessage(), exitProtocolMessage.getFullMessage());
	}

	@Test
	public void testWrongMessage()
	{
		ProtocolMessage message = ProtocolMessage.getProtocolMessage("asdasdf;qwerty");
		assertEquals("asdasdf;qwerty", message.getFullMessage());
		assertTrue(message instanceof UnknownProtocolMessage);
	}

}
