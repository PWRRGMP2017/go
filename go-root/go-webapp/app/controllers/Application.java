package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.PlayerRoom;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;

public class Application extends Controller
{
	/**
	 * Displays the home page.
	 */
	public static Result index()
	{
		return ok(views.html.index.render());
	}

	/**
	 * Displays the settings page.
	 */
	public static Result gameSettings(String playerName)
	{
		if (playerName == null || playerName.trim().isEmpty())
		{
			flash("error", "Please, choose a valid username.");
			return redirect(routes.Application.index());
		}

		return ok(views.html.gameSettings.render(playerName));
	}

	/**
	 * Loads the settings JavaScript file.
	 */
	public static Result gameSettingsJS(String playerName)
	{
		return ok(views.js.gameSettings.render(playerName));
	}

	/**
	 * Handle the player WebSocket.
	 */
	public static WebSocket<JsonNode> joinPlayerRoom(final String playerName)
	{
		return new WebSocket<JsonNode>()
		{
			// Called when the WebSocket Handshake is done.
			public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out)
			{
				if (!PlayerRoom.tryJoin(playerName, in, out))
				{
					ObjectNode response = Json.newObject();
					response.put("type", "error");
					response.put("message", "Your name is already taken.");
					out.write(response);
					out.close();
				}
			}
		};
	}
	
	/**
	 * Get a game log file.
	 */
	public static Result getGameLog(final String fileName)
	{
		File logFile = new File("public/gamelogs/" + fileName);
		try
		{
			return ok(new FileInputStream(logFile)).as("text/plain");
		}
		catch (FileNotFoundException e)
		{
			return notFound();
		}
	}
}
