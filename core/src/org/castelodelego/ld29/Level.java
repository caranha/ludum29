package org.castelodelego.ld29;

import com.badlogic.gdx.graphics.Color;

public class Level {
	public String topimage;
	public String bottomimage;
	public int enemyweight;
	public Color enemytint;
	
	public Level(String top, String bottom, int enemy, Color tint)
	{
		topimage = top;
		bottomimage = bottom;
		enemyweight = enemy;
		enemytint = tint;
	}
}
