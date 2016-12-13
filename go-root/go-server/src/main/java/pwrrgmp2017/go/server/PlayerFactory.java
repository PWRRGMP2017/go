package pwrrgmp2017.go.server;

import java.io.IOException;
import java.net.Socket;

public class PlayerFactory
{
	
	PlayerConnection create(Socket socket) throws IOException
	{
		PlayerConnection player= new RealPlayerConnection(socket);
		return player;
	}
}
