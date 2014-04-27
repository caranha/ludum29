package org.castelodelego.ld29.gameplay;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

public class GameKeyboardListener extends InputAdapter {
	GameplayScreen parent;
	boolean shift = false;
	int keymovex = 0;
	int keymovey = 0;
	
	
	public GameKeyboardListener(GameplayScreen p)
	{
		super();
		parent = p;
		shift = false;
	}
	
	public void reset()
	{
		keymovex = 0;
		keymovey = 0;
		shift = false;
	}
	
	@Override
	public boolean keyDown (int keycode) {
		switch (keycode)
		{
		case Keys.SHIFT_LEFT:
			shift = true;
			return true;
			
		case Keys.UP:
			if (shift)
				parent.sendFling(0, 1);
			else
			{
				keymovey = 20;
				parent.sendKeyMoveTouch(keymovex, keymovey);
			}
			return true;
				
		case Keys.DOWN:
			if (shift)
				parent.sendFling(0, -1);
			else
			{
				keymovey = -20;
				parent.sendKeyMoveTouch(keymovex, keymovey);
			}
			return true;
			
		case Keys.LEFT:
			if (shift)
				parent.sendFling(-1, 0);
			else
			{
				keymovex = -20;
				parent.sendKeyMoveTouch(keymovex, keymovey);
			}
			return true;
		case Keys.RIGHT:
			if (shift)
				parent.sendFling(1, 0);
			else
			{
				keymovex = 20;
				parent.sendKeyMoveTouch(keymovex, keymovey);
			}
			return true;
			
		}
		
		
		return false;
	}

	@Override
	public boolean keyUp (int keycode) {
		switch (keycode)
		{
		case Keys.SHIFT_LEFT:
			shift = false;
			return true;
		case Keys.UP:
		case Keys.DOWN:
			keymovey = 0;
			parent.sendKeyMoveTouch(keymovex, keymovey);
			return true;
		case Keys.LEFT:
		case Keys.RIGHT:
			keymovex = 0;
			parent.sendKeyMoveTouch(keymovex, keymovey);
			return true;
		}
		return false;
	}	
	
}
