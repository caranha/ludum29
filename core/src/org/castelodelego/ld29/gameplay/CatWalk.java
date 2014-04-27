package org.castelodelego.ld29.gameplay;

import org.castelodelego.ld29.Globals;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Array;


/***
 * This class  manages a catwalk - a line of segments that define a playing area.
 * 
 * @author caranha
 *
 */
public class CatWalk {
	
	static final TiledDrawable line_jagged_v = new TiledDrawable(((TextureAtlas) Globals.manager.get("images/pack.atlas", TextureAtlas.class)).findRegion("line_jagged"));
	static final TiledDrawable line_jagged_h = new TiledDrawable(((TextureAtlas) Globals.manager.get("images/pack.atlas", TextureAtlas.class)).findRegion("line_jagged_h"));
	static final TiledDrawable line_simple_v = new TiledDrawable(((TextureAtlas) Globals.manager.get("images/pack.atlas", TextureAtlas.class)).findRegion("line_simple"));
	static final TiledDrawable line_simple_h = new TiledDrawable(((TextureAtlas) Globals.manager.get("images/pack.atlas", TextureAtlas.class)).findRegion("line_simple_h"));
	
	
	Array<Vector2> original;
	Array<Vector2> points;
	
	double original_area;
	double areacache;
	double pathlength;
	
	Vector2 startPosition;
	
	TextureRegion background_top_region;
	TextureRegion background_bottom_region;
	
	PolygonSprite background_top;
	PolygonSprite background_bottom;
	
	
	
	public CatWalk(Array<Vector2> startingpoints, String topimage, String bottomimage)
	{
		points = new Array<Vector2>(true, 100, Vector2.class);
		original = new Array<Vector2>(true, 100, Vector2.class);
		
		for (Vector2 point:startingpoints)
		{
			points.add(point.cpy());
			original.add(point.cpy());
		}
			
		original_area = OgamMath.calcPolygonArea(original);
		areacache = OgamMath.calcPolygonArea(points);
		pathlength = OgamMath.calcPolygonLength(points);
		startPosition = points.first();
		
		// FIXME: Have to dispose these guys
		background_top_region = ((TextureAtlas) Globals.manager.get("images/pack.atlas", TextureAtlas.class)).findRegion(topimage);
		background_bottom_region = ((TextureAtlas) Globals.manager.get("images/pack.atlas", TextureAtlas.class)).findRegion(bottomimage);
		
		background_top = new PolygonSprite(createPolygonRegion(background_top_region,points));
		background_bottom = new PolygonSprite(createPolygonRegion(background_bottom_region,original));
		
	}
	
	PolygonRegion createPolygonRegion(TextureRegion img, Array<Vector2> area)
	{
		EarClippingTriangulator triangulator = new EarClippingTriangulator();
		
		
		float[] vertexlist = new float[area.size*2];
		for (int i = 0; i < area.size; i++)
		{
			vertexlist[2*i] = area.get(i).x;
			vertexlist[(2*i)+1] = area.get(i).y;
		}
		return (new PolygonRegion(img, vertexlist, triangulator.computeTriangles(vertexlist).items));
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

		i = idx_o;
		if (distance < pathlength/2) // possibly goes around all segments #4
		{
			while (i != idx_e)
			{
				i = (i+1)%points.size;
				candidate.add(points.get(i).cpy());
			}
		}
		else
		{
			while (i != idx_e)
			{
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
	
	public double getCoverage()
	{
		return areacache/original_area;
	}
	
	public Vector2 getStartPosition()
	{
		return startPosition;
	}
	
	public Vector2[] asArray()
	{
		return points.items;
	}
	
	public void render(PolygonSpriteBatch batch)
	{
		background_bottom.draw(batch);
		batch.flush(); // Black magick. Somehow if I don't flush the batch, one polygon sprite will influence the other.
		background_top.draw(batch);
	}
	
	public void renderPath(Batch b)
	{
		Vector2 prev;
		
		prev = original.peek();		
		for (Vector2 next: original)
		{
			if (Math.abs(prev.x - next.x) < 0.1f) 
			{  

				float ylow = Math.min(prev.y, next.y);
				float yhigh = Math.max(prev.y, next.y);
				float x = prev.x;
				line_jagged_v.draw(b, x-5, ylow, 10, yhigh-ylow);
			}
			else
			{
				float xlow = Math.min(prev.x, next.x);
				float xhigh = Math.max(prev.x, next.x);
				float y = prev.y;

				line_jagged_h.draw(b, xlow, y-5, xhigh-xlow,10);
			}
			prev = next;
		}
		
		prev = points.peek();		
		for (Vector2 next: points)
		{
			if (Math.abs(prev.x - next.x) < 0.1f) 
			{  

				float ylow = Math.min(prev.y, next.y);
				float yhigh = Math.max(prev.y, next.y);
				float x = prev.x;
				line_simple_v.draw(b, x-5, ylow, 10, yhigh-ylow);
			}
			else
			{
				float xlow = Math.min(prev.x, next.x);
				float xhigh = Math.max(prev.x, next.x);
				float y = prev.y;

				line_simple_h.draw(b, xlow, y-5, xhigh-xlow,10);
			}
			prev = next;
		}
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
	 * Test all enemies for collision, and signal them when they have collided with the catwalk
	 * @param enemies
	 */
	public void collideEnemies(Array<SimpleEnemy> enemies)
	{
		Vector2 prev = points.peek();
		for (Vector2 cur:points)
		{
			for (SimpleEnemy e: enemies)
			{
				Vector2 disp = new Vector2();
				float disp_s = Intersector.intersectSegmentCircleDisplace(prev, cur, e.getPos(), e.getRadius(), disp);
				if (disp_s < Float.POSITIVE_INFINITY)
					e.collide(disp,disp_s);
			}
			prev = cur;
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

	/**
	 * Pushes a cutline into the CatWalk. The catwalk will be cut in the future.
	 * 
	 * @param line
	 */
	public void pushCutline(Array<Vector2> line, Array<SimpleEnemy> enemies) {
		
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
			
			Array<Vector2> p1 = new Array<Vector2>(true, 40, Vector2.class);
			Array<Vector2> p2 = new Array<Vector2>(true, 40, Vector2.class);
			
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

			
			/** TODO: Select 1 or 2 based on enemies **/
			double p1area = OgamMath.calcPolygonArea(p1);
			double p2area = OgamMath.calcPolygonArea(p2);
			double p1weight = 0;
			double p2weight = 0;
			
			Array<SimpleEnemy> e1 = new Array<SimpleEnemy>();
			Array<SimpleEnemy> e2 = new Array<SimpleEnemy>();
			for (SimpleEnemy e: enemies)
			{
				if (OgamMath.isPointInPolygon(e.pos, p1))
				{
					e1.add(e);
					p1weight+= e.weight;
				}
				else
				{
					e2.add(e);
					p2weight+= e.weight;
				}
			}
			
			if (p1weight > p2weight || ( p1weight == p2weight && p1area > p2area))
			{
				points = p1;
				areacache = p1area;
				for (SimpleEnemy e: e2)
					e.kill();
			}
			else	
			{
				points = p2;
				areacache = p2area;
				for (SimpleEnemy e: e1)
					e.kill();
			}
			
			pathlength = OgamMath.calcPolygonLength(points);
			startPosition = points.first();
			background_top.setRegion(createPolygonRegion(background_top_region,points));
			Globals.log.addMessage("Coverage", "Coverage: "+getCoverage());
	}
}
