package org.castelodelego.ld29.gameplay;

import java.util.Iterator;

import org.castelodelego.ld29.Globals;
import org.castelodelego.ld29.Level;
import org.castelodelego.ld29.Prop;
import org.castelodelego.ld29.LD29Game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
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

	/* Rendering Related */
	OrthographicCamera gameCam;
	PolygonSpriteBatch polygonbatch;
	SpriteBatch batch;

	Sprite background;
	Sprite foreground;
	Color proptint;

	Sprite lifecount;
	
	
	/* State data */ 
	enum GameStates {PLAY, GAMEOVER, VICTORY};
	GameStates state = GameStates.PLAY;
	
	float fadein_timer = 0.5f;
	float fadeout_timer = 1.0f;
	float victory_timer = 2.0f;
	
	
	/* debug variables */
	ShapeRenderer debugrender;

	/* Control related */
	Vector2 touchpoint;
	Vector2 projectpoint;
	Vector2 keymove;	
	InputProcessor gesture;
	InputProcessor keyboard;

	
	/* Gameplay variables */
	CatWalk catwalkPath;
	Array<SimpleEnemy> enemies;
	Array<Prop> props;
	Array<PlayerShip> players;
	
	/* Sound */
	Sound victory;
	
	
	public GameplayScreen()
	{
		gameCam = new OrthographicCamera();
		debugrender = new ShapeRenderer();
		polygonbatch = new PolygonSpriteBatch();
		batch = new SpriteBatch();
		
		gesture = new GestureDetector(new GameTouchListener(gameCam,this));
		keyboard = new GameKeyboardListener(this);
		
		enemies = new Array<SimpleEnemy>(false,30,SimpleEnemy.class);
		props = new Array<Prop>(false,200,Prop.class);
		players = new Array<PlayerShip>(false,1,PlayerShip.class);
	}

	public void reset(Level newlevel)
	{

		// loading level info
		String topimage = newlevel.topimage;
		String bottomimage = newlevel.bottomimage;
		int totalweight = newlevel.enemyweight;
		proptint = newlevel.enemytint;


		// starting up variables
		// TODO: replace Debuglevel with something more sane
		// Debuglevel controls the catwalk geometry
		gameCam.setToOrtho(false, 800, 480); // 480,800 is the size of the "virtual" play area
		touchpoint = new Vector2();
		projectpoint = new Vector2();
		keymove = new Vector2();

		background = ((TextureAtlas) Globals.manager.get("images/pack.atlas", TextureAtlas.class)).createSprite(topimage);
		foreground = ((TextureAtlas) Globals.manager.get("images/pack.atlas", TextureAtlas.class)).createSprite(bottomimage);
		lifecount = ((TextureAtlas) Globals.manager.get("images/pack.atlas", TextureAtlas.class)).createSprite("anim/scissors_stop");
		catwalkPath = new CatWalk(DebugLevel.simpleRectangle(),topimage,bottomimage);
		
		// clearing things
		enemies.clear();
		for (Prop p:props)
			Globals.propPool.free(p);
		props.clear();
		players.clear();
		
		// adding enemies
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
		
		state = GameStates.PLAY;
		fadein_timer = 0.5f;
		fadeout_timer = 1.0f;
		victory_timer = 3.0f;
		
		victory = Globals.manager.get("sounds/Victory.ogg",Sound.class);
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
		if (players.size > 0)
		{
			touchpoint.set(posx, posy);
			projectpoint = catwalkPath.closestPoint(touchpoint);
			catwalkPath.shortestPath(players.peek().getPos(), projectpoint);
			players.peek().MoveTo(catwalkPath.shortestPath(players.peek().getPos(), projectpoint));
		}
	}
	void sendFling(int moveX, int moveY) {
		if (players.size > 0)
			players.peek().CutTo(moveX, moveY,catwalkPath);
	}

	
	private void update(float delta)
	{
		if (fadein_timer > 0)
			fadein_timer -= delta;
		
		switch (state)
		{
		case PLAY:
			if (players.size > 0)			
			{
				if (keymove.x != 0 || keymove.y != 0)
					sendTouch(players.peek().getPos().x + keymove.x, players.peek().getPos().y + keymove.y);

				// Player update returns true if the player died;
				if (players.peek().update(delta,catwalkPath,enemies))
				{
					Globals.gc.addLives(-1);
					players.clear();
				}
			}
			else // no players on the board, try to add a new player				
				if (Globals.gc.getLives() > 0)
					players.add(new PlayerShip(catwalkPath.getStartPosition().x, catwalkPath.getStartPosition().y, catwalkPath));
			
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
			
			if (Globals.gc.getLives() <= 0)
				state = GameStates.GAMEOVER;
			if (catwalkPath.getCoverage() < 0.2)
			{
				state = GameStates.VICTORY;
				victory.play(0.7f);
			}
			break;
			
			
		case GAMEOVER:
			fadeout_timer -= delta;
			Globals.gamesong.setVolume(Math.max(0, fadeout_timer));
			if (fadeout_timer < 0)
			{
				Globals.gc.init();
				((Game) Gdx.app.getApplicationListener()).setScreen(LD29Game.mainScreen);
			}
			break;
		case VICTORY:
			victory_timer -= delta;
			if (victory_timer < 0)
			{
				Globals.gc.addLives(1);
				Globals.gc.addLevel(1);	
				
				if (Globals.gc.getLevel() == 0)
					((Game) Gdx.app.getApplicationListener()).setScreen(LD29Game.mainScreen);
				else
					reset(Globals.levellist[Globals.gc.getLevel()]);
			}			
			break;
		}					
	}
	
	private void draw ()
	{
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		batch.setProjectionMatrix(gameCam.combined);
		debugrender.setProjectionMatrix(gameCam.combined);
		polygonbatch.setProjectionMatrix(gameCam.combined);		

		// DRAWING THE BACKGROUNDS
		polygonbatch.begin();
		background.draw(polygonbatch);
		catwalkPath.render(polygonbatch);
		polygonbatch.end();		
		
		// DRAWING THE SPRITES
		batch.begin();		
		catwalkPath.renderPath(batch);
		if (players.size > 0)
		{	
			players.peek().renderCutline(batch);
			players.peek().render(batch);
		}
		batch.setColor(proptint);
		for (Prop p: props)
			p.render(batch);
		for (int i = 0; i < Globals.gc.getLives(); i++)
			batch.draw(lifecount, 800-((i+2)*40), 440);
		
		
		if (state == GameStates.VICTORY)
		{
			foreground.draw(batch,Math.min(3-victory_timer,1));
		}			
		batch.end();
		
		// DRAWING FADE-INS AND FADE-OUTS
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		debugrender.begin(ShapeType.Filled);
		if (fadein_timer > 0)
		{
			debugrender.setColor(1,1,1,2f*fadein_timer);
			debugrender.rect(0, 0, gameCam.viewportWidth, gameCam.viewportWidth);		
		}
		if (state == GameStates.GAMEOVER)
		{

			debugrender.setColor(1,1,1,1-fadeout_timer);
			debugrender.rect(0, 0, gameCam.viewportWidth, gameCam.viewportWidth);
		}
		debugrender.end();
		// End Drawing Fade
		
		//debugRendering();
	}

	@SuppressWarnings("unused")
	private void debugRendering()
	{
		debugrender.setProjectionMatrix(gameCam.combined);
		debugrender.begin(ShapeType.Line);
		//catwalkPath.debugRender(debugrender);	
		
		//		if (players.size > 0)
		//			players.peek().debugRenderCutline(debugrender);
		
		debugrender.setColor(Color.WHITE);
		debugrender.circle(touchpoint.x, touchpoint.y, 5);
		debugrender.circle(projectpoint.x, projectpoint.y, 5);
		//for (SimpleEnemy e:enemies)
		//	e.debugRender(debugrender);
		debugrender.end();		
		//		debugrender.begin(ShapeType.Filled);
		//		if (players.size > 0)
		//			players.peek().debugRender(debugrender);
		//		debugrender.end();
	}
	
	
	
	
	
	@Override
	public void render(float delta) {
		update(delta);
		draw();
	}

	
	
	
	
	
	@Override
	public void resize(int width, int height) {
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
		((GameKeyboardListener) keyboard).reset();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		batch.dispose();
		polygonbatch.dispose();
		debugrender.dispose();
	}


}
