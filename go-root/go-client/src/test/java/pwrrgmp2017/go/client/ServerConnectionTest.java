package pwrrgmp2017.go.client;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidParameterException;
import java.util.Observable;
import java.util.Observer;

import org.junit.Test;

import pwrrgmp2017.go.clientserverprotocol.UnknownProtocolMessage;

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
		connection.connect();

		connection.send(message);
		assertEquals(connection.receive(), message);
		connection.close();
	}

	@Test
	public void testDataValidation()
	{
		boolean caughtException = false;
		try
		{
			new FakeServerConnection("", "");
		}
		catch (InvalidParameterException e)
		{
			// ok
			caughtException = true;
		}
		
		if (!caughtException)
		{
			fail();
		}
		caughtException = false;
		
		try
		{
			new FakeServerConnection("local", "12345a");
		}
		catch (InvalidParameterException e)
		{
			// ok
			caughtException = true;
		}
		
		if (!caughtException)
		{
			fail();
		}
		caughtException = false;
		
		try
		{
			new FakeServerConnection("localhost", "2");
		}
		catch (InvalidParameterException e)
		{
			// ok
			caughtException = true;
		}
		
		if (!caughtException)
		{
			fail();
		}
		caughtException = false;
		
		try
		{
			new FakeServerConnection("localhost", "5002");
		}
		catch (InvalidParameterException e)
		{
			fail();
		}
		
		
	}
	
	@Test
	public void testServerConnectionThread() throws IOException, InterruptedException
	{
		// Set up
		FakeServerConnection fakeServerConnection = new FakeServerConnection("localhost", PORT);
		ServerConnection serverConnection = fakeServerConnection;
		Observer observer = new Observer()
		{
			@Override
			public void update(Observable o, Object arg)
			{
				if (arg instanceof IOException)
				{
					System.out.println(((IOException) arg).getMessage());
				}
			}
		};
		Observer observerSpy = spy(observer);
		
		serverConnection.addObserver(observerSpy);
		serverConnection.startReceiving();
		assertTrue(serverConnection.getThread().isAlive());
		
		// Observer should receive a message
		fakeServerConnection.sendMessageFromFakeServer(new UnknownProtocolMessage("a message").getFullMessage());
		
		Thread.sleep(100);
		verify(observerSpy).update(eq(serverConnection), isA(UnknownProtocolMessage.class));
	
		// Simulate closing the connection by server
		fakeServerConnection.simulateServerClosedConnection();
		
		Thread.sleep(100);
		verify(observerSpy).update(eq(serverConnection), isA(IOException.class));
		
		// The thread should stop running
		Thread.sleep(100);
		assertFalse(serverConnection.getThread().isAlive());
		
		// When WE close the connection, we don't want any notifications
		serverConnection.connect();
		serverConnection.startReceiving();
		serverConnection.close();
		
		Thread.sleep(100);
		verify(observerSpy, times(2)).update(any(), any());
		assertFalse(serverConnection.getThread().isAlive());
		
		// Simulate an error on server
		serverConnection.connect();
		serverConnection.startReceiving();
		fakeServerConnection.simulateServerError();
		
		Thread.sleep(100);
		verify(observerSpy, times(2)).update(any(), isA(IOException.class));
		assertFalse(serverConnection.getThread().isAlive());
	}

}
