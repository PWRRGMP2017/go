package pwrrgmp2017.go.client;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidParameterException;

import org.junit.Test;

public class ServerConnectionTest
{
	final static int PORT = 5002;

	@Test
	public void testSendReceive() throws IOException
	{
		String message = "TEST MESSAGE";

		final Socket mockSocket = mock(Socket.class);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(message.getBytes());
		when(mockSocket.getOutputStream()).thenReturn(byteArrayOutputStream);
		when(mockSocket.getInputStream()).thenReturn(byteArrayInputStream);

		ServerConnection connection = new ServerConnection("localhost", PORT)
		{
			@Override
			protected void createSocket() throws IOException
			{
				this.socket = mockSocket;
			}
		};

		connection.send(message);
		assertEquals(connection.receive(), message);
		connection.close();
	}

	@Test(expected = InvalidParameterException.class)
	public void testDataValidation() throws InvalidParameterException, IOException
	{
		new ServerConnection("local", "12345a");
	}

}
