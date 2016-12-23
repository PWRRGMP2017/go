package pwrrgmp2017.go.client;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FakeServerConnection extends ServerConnection
{
	private volatile ConcurrentLinkedQueue<String> messages;
	private volatile boolean isClosed;
	private volatile boolean isServerClosed;
	private volatile boolean isServerError;

	public FakeServerConnection(String hostname, int port) throws InvalidParameterException
	{
		super(hostname, port);
		messages = new ConcurrentLinkedQueue<String>();
		isClosed = false;
		isServerClosed = false;
		isServerError = false;
	}
	
	public FakeServerConnection(String hostname, String port)
	{
		super(hostname, port);
		messages = new ConcurrentLinkedQueue<String>();
		isClosed = false;
		isServerClosed = false;
		isServerError = false;
	}
	
	@Override
	public void connect() throws IOException
	{
		isClosed = false;
		isServerClosed = false;
		isServerError = false;
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
	public synchronized String receive() throws IOException
	{
		while (messages.isEmpty() && !isServerClosed)
		{
			if (isClosed())
			{
				throw new IOException("Connection is closed");
			}
			
			if (isServerError)
			{
				throw new IOException("Server error");
			}
		}
		return messages.poll();
	}
	
	@Override
	public void send(String message)
	{
		return;
	}
	
	public void sendMessageFromFakeServer(String message)
	{
		messages.add(message);
	}
	
	public void simulateServerClosedConnection()
	{
		isServerClosed = true;
	}
	
	public void simulateServerError()
	{
		isServerError = true;
	}
}
