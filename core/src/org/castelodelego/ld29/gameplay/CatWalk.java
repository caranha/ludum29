package org.castelodelego.ld29.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
	
	Array<Vector2> original;
	Array<Vector2> points;
	
	double original_area;
	double areacache;
	double pathlength;
	
	Vector2 startPosition;
	
	
	public CatWalk(Array<Vector2> startingpoints)
	{
		points = new Array<Vector2>(true, 30, Vector2.class);
		original = new Array<Vector2>(true, 30, Vector2.class);
		
		for (Vector2 point:startingpoints)
		{
			points.add(point.cpy());
			original.add(point.cpy());
		}
			
		original_area = OgamMath.calcPolygonArea(original);
		areacache = OgamMath.calcPolygonArea(points);
		pathlength = OgamMath.calcPolygonLength(points);
		startPosition = points.first();
	}
	
	public void setStartPosition(Vector2 pos)
	{
		startPosition = closestPoint(pos);
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
	
	public Array<Vector2> shortestPath(Vector2 startorig, Vector2 end)
	{
		Vector2 start = closestPoint(startorig); 
		
		Array<Vector2> candidate = new Array<Vector2>(true, points.size+2, Vector2.class);
				
		int idx_o = -1;
		int idx_e = -1;

		// 1- Calculate the segments in which point 1 and 2 are located.
		for (int i = 0; i < points.size; i++) // possibly goes around all segments #2
		{
			if (idx_o == -1)
				if (testPointInSegment(i,start))
					idx_o = i;
			
			if (idx_e == -1)
				if (testPointInSegment(i,end))
					idx_e = i;
				
			if (idx_o != -1 && idx_e != -1) // these are the droids we are looking for
				break;
		}
				
		// 1a- If both points are in the same segment, set the end point as destination and return
		if (idx_o == idx_e)
		{
			candidate.add(end);
			return candidate;
		}
		
		// 2- Calculate the in-path distance from 1 to 2
		float distance = OgamMath.manhattanDistance(start, points.get((idx_o+1)%points.size));
		
		int i = ((idx_o+1)%points.size);
		while (i != idx_e) // possibly goes around all segments #3
		{
			distance += OgamMath.manhattanDistance(points.get(i), points.get((i+1)%points.size));
			i = (i+1)%points.size;
		}
		distance += OgamMath.manhattanDistance(points.get(i), end);

		// 3- Put all vectors in the path - order depends on the shorter distance

		Gdx.app.log("PathFinding", "Start Path Finding");
		
		i = idx_o;
		if (distance < pathlength/2) // possibly goes around all segments #4
		{
			while (i != idx_e)
			{
				i = (i+1)%points.size;
				
				Gdx.app.log("PathFinding", "Adding Point "+i);
				candidate.add(points.get(i).cpy());
			}
		}
		else
		{
			while (i != idx_e)
			{
				Gdx.app.log("PathFinding", "Adding Point "+i);
				candidate.add(points.get(i).cpy());
				
				i = (i - 1 + points.size)%points.size;
			}			
		}
		
		candidate.add(end);
		
		return candidate;
		
		
	}
	
	/**
	 * Returns true if the point "point" is in the segment between the path points idx and idx+1.
	 * Returns false if idx is out of range;
	 * 
	 * @param idx
	 * @param point
	 * @return
	 */
	boolean testPointInSegment(int idx, Vector2 point)
	{
		if (idx >= points.size)
			return false;
		
		Vector2 p0 = points.get(idx);
		Vector2 p1 = points.get((idx+1)%points.size);
		
		return OgamMath.isPointInSegment(p0, p1, point);
	}
	
	
	/**
	 * Returns the total area of this CatWalk
	 * @return
	 */
	public double getArea()
	{
		return areacache;
	}
	public double getLength()
	{
		return pathlength;
	}
	
	public Vector2 getStartPosition()
	{
		return startPosition;
	}
	
	public Vector2[] asArray()
	{
		return points.items;
	}
	
	public void debugRender(ShapeRenderer renderer)
	{

		Vector2 startpoint;
		
		renderer.setColor(Color.GRAY);
		startpoint = original.peek();		
		for (Vector2 next: original)
		{
			renderer.line(startpoint, next);
			startpoint = next;
		}
		
		renderer.setColor(Color.GREEN);
		startpoint = points.peek();		
		for (Vector2 next: points)
		{
			renderer.line(startpoint, next);
			startpoint = next;
		}
		

	}
	
	/**
	 * Tests if a line segment crosses the Catwalk
	 * 
	 * @param start first point of the segment to be tested
	 * @param end last point of the segment to be tested
	 * @return true if the segment crosses the polygon
	 */
	public int intersectSegmentCatwalk(Vector2 start, Vector2 end)
	{
		Vector2 prev = points.peek();
		int ret = 0;
		for (Vector2 cur: points)
		{
			if (Intersector.intersectSegments(start, end, prev, cur, null))
				ret++;
			prev = cur;
		}
		return ret;
	}

	public void cutCatwalk(Array<Vector2> line) {
		
			// First we find out where the end points are located;
			int startidx = -1, endidx = -1; 
			for (int i = 0; i < points.size; i++)
			{
				if (startidx != -1 && endidx != -1)
					break;
				if (startidx == -1 && OgamMath.isPointInSegment(points.get(i), points.get((i+1)%points.size),line.first()))
					startidx = i;
				if (endidx == -1 && OgamMath.isPointInSegment(points.get(i), points.get((i+1)%points.size),line.peek()))
					endidx = i;
			}
			
			// creating paths:
			Array<Vector2> p1 = new Array<Vector2>(true,30,Vector2.class);
			Array<Vector2> p2 = new Array<Vector2>(true,30,Vector2.class);
			
			// Adding nodes from main path
			int tmpidx;
			if (endidx == startidx) // special case, if both endpoints are in the same segment - only one gets the entire path
			{
				float enddist = OgamMath.manhattanDistance(points.get(endidx),line.peek());
				float startdist = OgamMath.manhattanDistance(points.get(startidx),line.first());
				
				Array<Vector2> tp;
				if (startdist < enddist) // start points come first
					tp = p1;
				else 
					tp = p2;

				for (int i = 0; i < points.size; i++)
				{
					tp.add(new Vector2(points.get((i + endidx + 1)%points.size)));
				}			
				
				
				// Adding nodes from cut:
				// TODO: I don't quite understand why the order must be reversed if start and end are in the same segment.
				// Study this black magic!
				for (int i = 0; i < line.size; i++)
					p1.add(new Vector2(line.get(i)));
				
				while (line.size > 0) // p2 nodes are added in reverse -- from end to start
					p2.add(line.pop());
			}
			else // both endpoints are in different segments: regular case
			{
				tmpidx = (endidx + 1)%points.size;
				while (tmpidx != (startidx + 1)%points.size)
				{
					p2.add(new Vector2(points.get(tmpidx)));
					tmpidx = (tmpidx + 1)%points.size;
				}
				
				tmpidx = (startidx + 1)%points.size;
				while (tmpidx != (endidx + 1)%points.size)
				{
					p1.add(new Vector2(points.get(tmpidx)));
					tmpidx = (tmpidx + 1)%points.size;
				}
				
				
				// Adding nodes from cut:
				for (int i = 0; i < line.size; i++)
					p2.add(new Vector2(line.get(i)));
				
				while (line.size > 0) // p1 nodes are added in reverse -- from end to start
					p1.add(line.pop());
			}
			
			// TODO: Have to take into account the presence of enemies, and send them "die" signals
			double p1area = OgamMath.calcPolygonArea(p1);
			double p2area = OgamMath.calcPolygonArea(p2);
			
			if (p1area > p2area)
			{
				points = p1;
				areacache = p1area;
			}
			else
			{
				points = p2;
				areacache = p2area;
			}
			
			pathlength = OgamMath.calcPolygonLength(points);
			startPosition = points.first();
			
	}
}
