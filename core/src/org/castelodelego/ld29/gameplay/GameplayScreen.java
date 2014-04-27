package org.castelodelego.ld29.gameplay;

import java.util.Iterator;

import org.castelodelego.ld29.Globals;
import org.castelodelego.ld29.Prop;
import org.castelodelego.ld29.LD29Game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GameplayScreen implements Screen {

	/* Systems */
	OrthographicCamera gameCam;
	ShapeRenderer debugrender;
	PolygonSpriteBatch polygonbatch;
	SpriteBatch batch;
	Sprite background;
	
	/* debug variables */
	Vector2 touchpoint;
	Vector2 projectpoint;
	
	/* Gameplay variables */
	CatWalk catwalkPath;
	PlayerShip cutter;
	Array<SimpleEnemy> enemies;
	Array<Prop> props;
	
	Vector2 keymove;
	
	InputProcessor gesture;
	InputProcessor keyboard;
	
	public GameplayScreen()
	{
		gameCam = new OrthographicCamera();
		debugrender = new ShapeRenderer();
		polygonbatch = new PolygonSpriteBatch();
		batch = new SpriteBatch();
		enemies = new Array<SimpleEnemy>(false,30,SimpleEnemy.class);
		props = new Array<Prop>(false,200,Prop.class);
		
		gesture = new GestureDetector(new GameTouchListener(gameCam,this));
		keyboard = new GameKeyboardListener(this);
	}

	public void reset()
	{

		gameCam.setToOrtho(false, 800, 480); // 480,800 is the size of the "virtual" play area
		touchpoint = new Vector2();
		projectpoint = new Vector2();
		keymove = new Vector2();
		
		// TODO: ADD reading from stage file;
		String topimage = "TopImageSample";
		String bottomimage = "BottomImageSample";
		int totalweight = 10;

		background = ((TextureAtlas) Globals.manager.get("images/pack.atlas", TextureAtlas.class)).createSprite(topimage);
		catwalkPath = new CatWalk(DebugLevel.simpleRectangle(),topimage,bottomimage);
		
		enemies.clear();
		
		for (Prop p:props)
			Globals.propPool.free(p);
		props.clear();
		
		Vector2 epos = new Vector2();
		while (totalweight > 0)
		{
			epos.x = Globals.dice.nextInt((int)gameCam.viewportWidth);
			epos.y = Globals.dice.nextInt((int)gameCam.viewportHeight);
			if (OgamMath.isPointInPolygon(epos, catwalkPath.points)) //FIXME: I shouldn't have direct access to this array
			{
				SimpleEnemy e = new SimpleEnemy(epos.x,epos.y);
				enemies.add(e);
				totalweight -= e.getWeight();
			}
		}
		
		cutter = new PlayerShip(catwalkPath.getStartPosition().x, catwalkPath.getStartPosition().y, catwalkPath);		
	}	
	
	public void addProp(Prop p)
	{
		props.add(p);
	}
	
	

	void sendKeyMoveTouch(float dirx, float diry)
	{
		keymove.x = dirx;
		keymove.y = diry;
	}	
	void sendTouch(float posx, float posy)
	{
		touchpoint.set(posx, posy);
		projectpoint = catwalkPath.closestPoint(touchpoint);
		catwalkPath.shortestPath(cutter.getPos(), projectpoint);
		cutter.MoveTo(catwalkPath.shortestPath(cutter.getPos(), projectpoint));
	}
	void sendFling(int moveX, int moveY) {
		cutter.CutTo(moveX, moveY,catwalkPath);
	}

	
	private void update(float delta)
	{
		
		if (keymove.x != 0 || keymove.y != 0)
			sendTouch(cutter.getPos().x + keymove.x, cutter.getPos().y + keymove.y);
		
		cutter.update(delta,catwalkPath,enemies);

		Iterator<SimpleEnemy> iter = enemies.iterator();
		while (iter.hasNext())
		{
			SimpleEnemy aux = iter.next();
			aux.update(delta);
			if (aux.isAlive() == false)
				iter.remove();
		}
		
		Iterator<Prop> iterProp = props.iterator();
		while (iterProp.hasNext())
		{
			Prop aux = iterProp.next();
			aux.update(delta);
			if (aux.isDead())
			{				
				iterProp.remove();
				Globals.propPool.free(aux);
			}
		}
		
		
		catwalkPath.collideEnemies(enemies);

		// End of worls conditions
		if (catwalkPath.getCoverage() < 0.4)
			((Game) Gdx.app.getApplicationListener()).setScreen(LD29Game.mainScreen);

	}
	
	private void draw ()
	{
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(gameCam.combined);
		
		
		polygonbatch.setProjectionMatrix(gameCam.combined);
		polygonbatch.begin();
		background.draw(polygonbatch);
		catwalkPath.render(polygonbatch);
		polygonbatch.end();		
		
		batch.begin();
		//background.draw(batch);
		for (Prop p: props)
			p.render(batch);
		batch.end();
		
		
		// DEBUG Renders
		debugrender.setProjectionMatrix(gameCam.combined);
		debugrender.begin(ShapeType.Line);
		catwalkPath.debugRender(debugrender);	
		cutter.debugRenderCutline(debugrender);

		debugrender.setColor(Color.WHITE);
		debugrender.circle(touchpoint.x, touchpoint.y, 5);
		debugrender.circle(projectpoint.x, projectpoint.y, 5);
		//for (SimpleEnemy e:enemies)
		//	e.debugRender(debugrender);
		debugrender.end();
		
		debugrender.begin(ShapeType.Filled);
		cutter.debugRender(debugrender);
		debugrender.end();
	}
	
	
	
	@Override
	public void render(float delta) {
		update(delta);
		draw();
	}

	
	
	
	
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		Globals.multiplexer.addProcessor(gesture);
		Globals.multiplexer.addProcessor(keyboard);
	}

	@Override
	public void hide() {
		Globals.multiplexer.removeProcessor(gesture);
		Globals.multiplexer.removeProcessor(keyboard);
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
		batch.dispose();
		polygonbatch.dispose();
		debugrender.dispose();
	}


}
