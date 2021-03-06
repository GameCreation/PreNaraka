package de.gamecreation.prenaraka.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface ScreenTransition {
	static final String TAG = ScreenTransition.class.getName();
	public float getDuration();

	public void render(SpriteBatch batch, Texture currScreen,
			Texture nextScreen, float alpha);
	
}
