package de.gamecreation.prenaraka.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;

import de.gamecreation.prenaraka.game.Assets;

public abstract class AbstractGameScreen implements Screen {
	private static final String TAG = AbstractGameScreen.class.getName();
	protected DirectedGame game;

	public AbstractGameScreen(DirectedGame game) {
		this.game = game;
	}

	public abstract void render(float deltaTime);

	public abstract void resize(int width, int height);

	public abstract void show();

	public abstract void hide();

	public abstract void pause();

	public abstract InputProcessor getInputProcessor(); // Screen Transitions

	public void resume() {
		Assets.instance.init(new AssetManager());
	}

	public void dispose() {
		Assets.instance.dispose();
	}
}
