package models.msgs;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.WebSocket;

public class Join
{
	public final String name;
	public final WebSocket.In<JsonNode> in;
	public final WebSocket.Out<JsonNode> out;

	public Join(String name, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out)
	{
		this.name = name;
		this.in = in;
		this.out = out;
	}
}
