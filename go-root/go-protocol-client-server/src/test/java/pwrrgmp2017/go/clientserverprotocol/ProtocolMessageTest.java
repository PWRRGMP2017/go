package pwrrgmp2017.go.clientserverprotocol;

import static org.junit.Assert.*;

import java.security.InvalidParameterException;
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
	}

	@Test
	public void testLoginProtocolMessage()
	{
		LoginProtocolMessage sentMessage = new LoginProtocolMessage("User 1234");
		assertEquals("User 1234", sentMessage.getUsername());

		ProtocolMessage receivedMessage = ProtocolMessage.getProtocolMessage(sentMessage.getFullMessage());
		assertEquals(sentMessage.getUsername(), ((LoginProtocolMessage) (receivedMessage)).getUsername());
	}

	@Test(expected = InvalidParameterException.class)
	public void testWrongMessage()
	{
		ProtocolMessage.getProtocolMessage("asdasdf;qwerty");
	}

}
