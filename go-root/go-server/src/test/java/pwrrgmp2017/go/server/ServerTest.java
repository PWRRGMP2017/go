package pwrrgmp2017.go.server;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.Test;

public class ServerTest
{

	@Test
	public void testServerAndPlayerConnection() throws IOException
	{
		Server server = new Server(0);
		assertTrue(server.getPort() != 0);
		Socket clientSocket = new Socket("localhost", server.getPort());
		assertTrue(clientSocket.isConnected());
		BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

		output.println("Test");
		assertEquals("Received: Test", input.readLine());

		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		server.close();
		clientSocket.close();

		assertEquals("exit", input.readLine());
	}

}
