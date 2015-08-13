package de.gamecreation.prenaraka;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Interpolation;

import de.gamecreation.prenaraka.game.Assets;
import de.gamecreation.prenaraka.screens.DirectedGame;
import de.gamecreation.prenaraka.screens.MenuScreen;
import de.gamecreation.prenaraka.screens.ScreenTransition;
import de.gamecreation.prenaraka.screens.transitions.ScreenTransitionSlice;
import de.gamecreation.prenaraka.util.GamePreferences;

public class Main extends DirectedGame {
	private static final String TAG = Main.class.getName();

	private final PlatformSpecificCode platSpeCode;

	public Main(PlatformSpecificCode platSpeCode) {
		this.platSpeCode = platSpeCode;
	}

	@Override
	public void create() {
		// Set Libgdx log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		// Load assets
		Assets.instance.init(new AssetManager());

		// Load preferences for audio settings and start playing music
		GamePreferences.instance.load();
		// AudioManager.instance.play((Sound)
		// Assets.instance.getObjectFromAssetManager("song01"));
		// //Assets.instance.music.song01

		// Start game at menu screen
		ScreenTransition transition = ScreenTransitionSlice.init(2,
				ScreenTransitionSlice.UP_DOWN, 10, Interpolation.pow5Out);
		setScreen(new MenuScreen(this), transition);
	}
}
