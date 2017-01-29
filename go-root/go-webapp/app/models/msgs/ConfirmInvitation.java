package models.msgs;

import models.Game;

public class ConfirmInvitation {
	public final Game game;
	public ConfirmInvitation(final Game game) {
		this.game = game;
	}
}
