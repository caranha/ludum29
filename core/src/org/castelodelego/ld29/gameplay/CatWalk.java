package org.castelodelego.ld29.gameplay;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;


/***
 * This class  manages a catwalk - a line of segments that define a playing area.
 * 
 * @author caranha
 *
 */
public class CatWalk {
	
	Array<Vector2> points;
	double areacache;
	
	
	public CatWalk(Array<Vector2> startingpoints)
	{
		points = new Array<Vector2>(true, 30, Vector2.class);
		for (Vector2 point:startingpoints)
			points.add(point.cpy());
		areacache = OgamMath.calcPolygonArea(points);
	}
	
	
	
	/**
	 * Returns the closest point, in the catwalk, to the point passed as a parameter.
	 * 
	 * @param target 
	 * @return
	 */
	public Vector2 closestPoint(Vector2 target)
	{
		float dist = -1;		
		Vector2 closestart = new Vector2();
		Vector2 closeend = new Vector2();
		
		Vector2 prev = points.peek();
		for (Vector2 cur: points)
		{
			float vectordistance = Intersector.distanceSegmentPoint(prev, cur, target);
			if (dist == -1 || dist > vectordistance)
			{
				dist = vectordistance;
				closestart = prev;
				closeend = cur;
			}
			prev = cur;
		}
		
		Vector2 ret = new Vector2();
		ret = Intersector.nearestSegmentPoint(closestart, closeend, target, ret);
		return ret;
	}
	
	/**
	 * Returns the total area of this CatWalk
	 * @return
	 */
	public double getArea()
	{
		return areacache;
	}
	
	
	public void debugRender(ShapeRenderer renderer)
	{
		Vector2 startpoint = points.peek();
		
		renderer.begin(ShapeType.Line);
		renderer.setColor(Color.RED);
		for (Vector2 next: points)
		{
			renderer.line(startpoint, next);
			startpoint = next;
		}
		renderer.end();
	}
	
	
	
	
}
