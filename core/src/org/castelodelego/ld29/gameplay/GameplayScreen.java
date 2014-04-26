package org.castelodelego.ld29.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GameplayScreen implements Screen {

	OrthographicCamera gameCam;
	ShapeRenderer debugrender;
	
	/* debug variables */
	Vector2 touchpoint;
	Vector2 projectpoint;
	
	
	CatWalk base;
	CatWalk current;
	PlayerShip cutter;
	
	public GameplayScreen()
	{
		gameCam = new OrthographicCamera();
		gameCam.setToOrtho(false);
		debugrender = new ShapeRenderer();
	}

	public void reset()
	{
		base = new CatWalk(DebugLevel.simpleRectangle());
		current = new CatWalk(DebugLevel.simpleRectangle());
		cutter = new PlayerShip(current.getStartingPoint().x, current.getStartingPoint().y);
		
		touchpoint = new Vector2();
		projectpoint = new Vector2();
	}	
	
	
	
	public void sendTouch(float posx, float posy)
	{
		touchpoint.set(posx, posy);
		projectpoint = base.closestPoint(touchpoint);
		current.shortestPath(cutter.getPos(), projectpoint);
		//cutter.setPos(projectpoint.x, projectpoint.y);

		
		cutter.MoveTo(current.shortestPath(cutter.getPos(), projectpoint));
	}
	
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		
		/** Updating **/
		cutter.update(delta);
		
		
		/** Rendering **/
		debugrender.setProjectionMatrix(gameCam.combined);
		
		debugrender.begin(ShapeType.Line);
		
		debugrender.setColor(Color.RED);
		base.debugRender(debugrender);
		debugrender.setColor(Color.GREEN);
		current.debugRender(debugrender);
		
		cutter.debugRender(debugrender);
		
		debugrender.setColor(Color.WHITE);
		debugrender.circle(touchpoint.x, touchpoint.y, 5);
		debugrender.circle(projectpoint.x, projectpoint.y, 5);
		
		
		debugrender.end();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(new GestureDetector(new GameTouchListener(gameCam,this)));
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
