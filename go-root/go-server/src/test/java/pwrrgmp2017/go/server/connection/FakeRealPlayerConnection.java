package pwrrgmp2017.go.server.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FakeRealPlayerConnection extends RealPlayerConnection
{

	private volatile ConcurrentLinkedQueue<String> messages;
	private volatile ConcurrentLinkedQueue<String> sentMessages;
	private volatile boolean isClosed;
	
	public FakeRealPlayerConnection(Socket socket) throws IOException
	{
		super();
		setUp();
	}

	public FakeRealPlayerConnection() throws IOException
	{
		super();
		setUp();
	}
	
	private void setUp()
	{
		messages = new ConcurrentLinkedQueue<>();
		sentMessages = new ConcurrentLinkedQueue<>();
		isClosed = false;
	}
	
	public void resetConnectionState()
	{
		isClosed = false;
	}
	
	@Override
	public void close()
	{
		isClosed = true;
	}
	
	@Override
	public boolean isClosed()
	{
		return isClosed;
	}
	
	@Override
	public String receive() throws IOException
	{
		while (messages.isEmpty())
		{
			if (isClosed())
			{
				throw new IOException("Connection is closed");
			}
		}
		return messages.poll();
	}
	
	@Override
	public void send(String message)
	{
		sentMessages.add(message);
	}
	
	public void sendMessageFromFakePlayer(String message)
	{
		messages.add(message);
	}
	
	public String receiveMessageAsFakePlayer() throws IOException
	{
		while (sentMessages.isEmpty()) 
		{
			if (isClosed() && sentMessages.isEmpty())
			{
				throw new IOException("Connection is closed");
			}
		}
		return sentMessages.poll();
	}

}
