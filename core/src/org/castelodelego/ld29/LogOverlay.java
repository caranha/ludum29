package org.castelodelego.ld29;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class LogOverlay {
	Map<String,String> messages;
	
	public LogOverlay()
	{
		messages = new HashMap<String,String>();
	}
	
	public void addMessage(String key, String message)
	{
		messages.put(key, message);
	}
	
	public void removeMessage(String key)
	{
		messages.remove(key);
	}

	public void render()
	{
		Globals.batch.setProjectionMatrix(LD29Game.globalcam.combined);
		Globals.batch.begin();
		Globals.debugtext.setColor(Color.YELLOW);		
		
		int lineskip = 0;
		for (String message:messages.values())
		{
			Globals.debugtext.draw(Globals.batch, message, 0, Gdx.graphics.getHeight() - lineskip);
			lineskip += 10;
		}
		Globals.batch.end();
	}
}
