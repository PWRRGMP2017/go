package models.msgs;

import com.fasterxml.jackson.databind.JsonNode;

public class SendBoard
{
	public final JsonNode json;
	public SendBoard(final JsonNode json) {
		this.json = json;
	}
}
