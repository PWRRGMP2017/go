package pwrrgmp2017.go.server.connection;

import java.io.IOException;

public abstract class PlayerConnection
{
	protected PlayerInfo playerInfo;

	protected PlayerConnection invitedPlayer = null;
	protected PlayerConnection invitingPlayer = null;

	public PlayerInfo getPlayerInfo()
	{
		return playerInfo;
	}

	public String getPlayerName()
	{
		return playerInfo.getName();
	}

	public void setPlayerName(String name)
	{
		playerInfo.setPlayerName(name);
	}

	public PlayerConnection getInvitedPlayer()
	{
		return invitedPlayer;
	}

	public PlayerConnection getInvitingPlayer()
	{
		return invitingPlayer;
	}

	public abstract void close();

	public abstract String receive() throws IOException;

	public abstract void send(String message);

	public boolean isInvited()
	{
		return invitingPlayer != null;
	}

	public boolean isInviting()
	{
		return invitedPlayer != null;
	}

	public boolean invitePlayer(PlayerConnection player)
	{
		if (isInvited() || isInviting())
		{
			return false;
		}

		invitedPlayer = player;
		return true;
	}

	public boolean inviteThisPlayerBy(PlayerConnection player)
	{
		if (isInvited() || isInviting())
		{
			return false;
		}

		invitingPlayer = player;
		return true;
	}

	public boolean cancelInvitation()
	{
		if (isInvited())
		{
			invitingPlayer = null;
			return true;
		}
		else if (isInviting())
		{
			invitedPlayer = null;
			return true;
		}
		else
		{
			return false;
		}
	}

}
