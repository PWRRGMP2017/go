package pwrrgmp2017.go.server.connection;

import java.io.IOException;

/**
 * Represents a connection with a player.
 */
public abstract class PlayerConnection
{
	/**
	 * Information about the player.
	 */
	protected PlayerInfo playerInfo;

	/**
	 * Reference to invited player (if this player is inviting).
	 */
	protected PlayerConnection invitedPlayer = null;

	/**
	 * Reference to inviting player (if this player is invited).
	 */
	protected PlayerConnection invitingPlayer = null;

	/**
	 * @return information about the player
	 */
	public PlayerInfo getPlayerInfo()
	{
		return playerInfo;
	}
	
	/**
	 * @return player name
	 */
	public String getPlayerName()
	{
		return playerInfo.getName();
	}

	/**
	 * Changes the player name.
	 * @param name new name
	 */
	public void setPlayerName(String name)
	{
		playerInfo.setPlayerName(name);
	}
	
	/**
	 * @return currently invited player (if this player is inviting)
	 */
	public PlayerConnection getInvitedPlayer()
	{
		return invitedPlayer;
	}
	
	/**
	 * @return currently inviting player (if this player is invited)
	 */
	public PlayerConnection getInvitingPlayer()
	{
		return invitingPlayer;
	}

	/**
	 * Closes connection with the player.
	 */
	public abstract void close();
	
	/**
	 * Waits for a message from the player.
	 * @return the message, or null if the player closed the connection
	 * @throws IOException if something went wrong
	 */
	public abstract String receive() throws IOException;
	
	/**
	 * Sends a message to the player.
	 * @param message the message to send
	 */
	public abstract void send(String message);

	/**
	 * @returns true if the player is invited by another player
	 */
	public boolean isInvited()
	{
		return invitingPlayer != null;
	}
	
	/**
	 * @return true if the player is inviting another player
	 */
	public boolean isInviting()
	{
		return invitedPlayer != null;
	}
	
	/**
	 * Sets the informations that this player is inviting another player.
	 * @param player invited player
	 * @return false if the player is already inviting or invited
	 */
	public boolean invitePlayer(PlayerConnection player)
	{
		if (isInvited() || isInviting())
		{
			return false;
		}

		invitedPlayer = player;
		return true;
	}

	/**
	 * Sets the informations that this player is invited by another player.
	 * @param player inviting player
	 * @return false if the player is already inviting or invited
	 */
	public boolean inviteThisPlayerBy(PlayerConnection player)
	{
		if (isInvited() || isInviting())
		{
			return false;
		}

		invitingPlayer = player;
		return true;
	}

	/**
	 * Removes information about any invitation.
	 * @return true if the player was inviting or invited
	 */
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
