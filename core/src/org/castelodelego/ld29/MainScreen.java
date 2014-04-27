package org.castelodelego.ld29;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import org.castelodelego.ld29.gameplay.GameplayScreen;

public class MainScreen implements Screen {

	Sprite background= null;
	SpriteBatch batch = new SpriteBatch();
	float timeout = 0;
	
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		background.draw(batch);
		batch.end();
		
		
		
		timeout += delta;
		if (timeout > 0.5 && Gdx.input.isTouched())
		{
			((GameplayScreen) LD29Game.gameplayScreen).reset(Globals.levellist[Globals.gc.getLevel()]);
			((Game) Gdx.app.getApplicationListener()).setScreen(LD29Game.gameplayScreen);
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		if (background == null)
			background = ((TextureAtlas) Globals.manager.get("images/pack.atlas", TextureAtlas.class)).createSprite("titlescreen");
		timeout = 0;
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
