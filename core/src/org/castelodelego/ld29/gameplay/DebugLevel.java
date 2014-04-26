package org.castelodelego.ld29.gameplay;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class DebugLevel {

	public static Array<Vector2> simpleRectangle()
	{
		Array<Vector2> ret = new Array<Vector2>();
		ret.add(new Vector2(50,50));
		//addnook(ret);
		ret.add(new Vector2(700,50));		
		ret.add(new Vector2(700,400));
		ret.add(new Vector2(50,400));
		return ret;
	}
	
	public static void addnook(Array<Vector2> ret)
	{
		ret.add(new Vector2(300,50));
		ret.add(new Vector2(300,100));
		ret.add(new Vector2(500,100));
		ret.add(new Vector2(500,50));
	}
}
