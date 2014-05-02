package org.castelodelego.ld29;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import org.castelodelego.ld29.gameplay.GameplayScreen;

public class MainScreen implements Screen {

	ShapeRenderer debugrender = new ShapeRenderer();
	
	Sprite background= null;
	SpriteBatch batch = new SpriteBatch();
	OrthographicCamera cam = new OrthographicCamera();
	float timeout = 0;
	
	
	@Override
	public void render(float delta) {
		cam.setToOrtho(false, 800, 480);
		Gdx.gl.glClearColor(1, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		background.draw(batch);
		batch.end();
		
		
		timeout += delta;
		if (timeout > 0.5 && Gdx.input.isTouched())
		{
			Vector3 touch = cam.unproject(new Vector3(Gdx.input.getX(),Gdx.input.getY(),0));
			if (!(touch.dst(new Vector3(690,110,0)) < 110))
				Globals.gc.addLevel(1);
			
			((GameplayScreen) LD29Game.gameplayScreen).reset(Globals.levellist[Globals.gc.getLevel()]);
			((Game) Gdx.app.getApplicationListener()).setScreen(LD29Game.gameplayScreen);
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		if (Globals.gamesong == null)
			Globals.init_music();
		Globals.gamesong.setLooping(true);
		Globals.gamesong.stop();
		Globals.gamesong.setVolume(1);
		Globals.gamesong.play();
		
		if (background == null)
			background = ((TextureAtlas) Globals.manager.get("images/pack.atlas", TextureAtlas.class)).createSprite("titlescreen2");
		timeout = 0;
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

}
