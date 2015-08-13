package de.gamecreation.prenaraka.game;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import de.gamecreation.prenaraka.screens.DirectedGame;
import de.gamecreation.prenaraka.screens.MenuScreen;
import de.gamecreation.prenaraka.screens.ScreenTransition;
import de.gamecreation.prenaraka.screens.transitions.ScreenTransitionSlide;
import de.gamecreation.prenaraka.util.CameraHelper;
import de.gamecreation.prenaraka.util.Constants;

public class WorldController extends InputAdapter implements Disposable {
	//TODO change collision detection to Box2D
	
	private static final String TAG = WorldController.class.getName();

	private DirectedGame game;

	public static CameraHelper cameraHelper;

	public Level level;
	public int lives;
	public int score;

	private float timeLeftGameOverDelay;

	// Rectangles for collision detection
	private Rectangle r1 = new Rectangle();
	private Rectangle r2 = new Rectangle();

	// Special Effects
	public float livesVisual;
	public float scoreVisual;

	// Box2D
	public World b2world;

	// Accelerometer
	private boolean accelerometerAvailable;

	

	private void initLevel() {
		score = 0;
		scoreVisual = score;
		level = new Level();
		cameraHelper.setTarget(level.hero);
		initPhysics();
	}

	public WorldController(DirectedGame game) {
		this.game = game;
		init();
	};

	private void init() {
		accelerometerAvailable = Gdx.input
				.isPeripheralAvailable(Peripheral.Accelerometer); // Accelerometer
		cameraHelper = new CameraHelper();
		lives = Constants.LIVES_START;
		livesVisual = lives;
		timeLeftGameOverDelay = 0;
		initLevel();
	}

	public void update(float deltaTime) {
		handleDebugInput(deltaTime);
		if (isGameOver()) {
			timeLeftGameOverDelay -= deltaTime;
			if (timeLeftGameOverDelay < 0)
				backToMenu();
		} else {
			handleInputGame(deltaTime);
		}
		level.update(deltaTime);
//		testCollisions();
		b2world.step(deltaTime, 8, 3);

		cameraHelper.update(deltaTime);
		if (!isGameOver()) {
//			AudioManager.instance.play(Assets.instance.getObjectFromAssetManager("liveLost"));
			lives--;
			if (isGameOver())
				timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
			else
				initLevel();
		}
		level.parallaxBG.updateScrollPosition(cameraHelper.getPosition());

		// Special Effects
		if (livesVisual > lives)
			livesVisual = Math.max(lives, livesVisual - 1 * deltaTime);
		if (scoreVisual < score)
			scoreVisual = Math.min(score, scoreVisual + 250 * deltaTime);
	}

	private void handleDebugInput(float deltaTime) {
		if (Gdx.app.getType() != ApplicationType.Desktop)
			return;

		if (!cameraHelper.hasTarget(level.hero)) {
			// Camera Controls (move)
			float camMoveSpeed = 5 * deltaTime;
			float camMoveSpeedAccelerationFactor = 5;
			if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
				camMoveSpeed *= camMoveSpeedAccelerationFactor;
			if (Gdx.input.isKeyPressed(Keys.LEFT))
				moveCamera(-camMoveSpeed, 0);
			if (Gdx.input.isKeyPressed(Keys.RIGHT))
				moveCamera(camMoveSpeed, 0);
			if (Gdx.input.isKeyPressed(Keys.UP))
				moveCamera(0, camMoveSpeed);
			if (Gdx.input.isKeyPressed(Keys.DOWN))
				moveCamera(0, -camMoveSpeed);
			if (Gdx.input.isKeyPressed(Keys.BACKSPACE))
				cameraHelper.setPosition(0, 0);
		}
		// Camera Controls (zoom)
		float camZoomSpeed = 1 * deltaTime;
		float camZoomSpeedAccelerationFactor = 5;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
			camZoomSpeed *= camZoomSpeedAccelerationFactor;
		if (Gdx.input.isKeyPressed(Keys.COMMA))
			cameraHelper.addZoom(camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.PERIOD))
			cameraHelper.addZoom(-camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.SLASH))
			cameraHelper.setZoom(1);

	}

	private void moveCamera(float x, float y) {
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);

	}

	@Override
	public boolean keyUp(int keycode) {
		// Reset game world
		if (keycode == Keys.R) {
			init();
			Gdx.app.debug(TAG, "Game world resetted");
		}
		// Toggle camera follow
		else if (keycode == Keys.ENTER) {
			cameraHelper.setTarget(cameraHelper.hasTarget() ? null
					: level.hero);
			Gdx.app.debug(TAG,
					"Camera follow enabled: " + cameraHelper.hasTarget());
		}
		// Back to Menu
		else if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
			backToMenu();
		}
		return false;
	}

	private Pixmap createProceduralPixmap(int width, int height) {
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
		// Fill square with red color at 50% opacity
		pixmap.setColor(1, 0, 0, 0.5f);
		pixmap.fill();
		// Draw a yellow-colored X shape on square
		pixmap.setColor(1, 1, 0, 1);
		pixmap.drawLine(0, 0, width, height);
		pixmap.drawLine(width, 0, 0, height);
		// Draw a cyan-colored border around square
		pixmap.setColor(0, 1, 1, 1);
		pixmap.drawRectangle(0, 0, width, height);
		return pixmap;
	}

	private void handleInputGame(float deltaTime) {
		if (cameraHelper.hasTarget(level.hero)) {
			// Player Movement
			if (Gdx.input.isKeyPressed(Keys.LEFT)) {
				level.hero.velocity.x = -level.hero.terminalVelocity.x;
			} else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
				level.hero.velocity.x = level.hero.terminalVelocity.x;
			} else {
				// Use accelerometer for movement if available
				if (accelerometerAvailable) {
					// normalize accelerometer values from [-10, 10] to [-1, 1]
					// which translate to rotations of [-90, 90] degrees
					float amount = Gdx.input.getAccelerometerY() / 10.0f;
					amount *= 90.0f;
					// is angle of rotation inside dead zone?
					if (Math.abs(amount) < Constants.ACCEL_ANGLE_DEAD_ZONE) {
						amount = 0;
					} else {
						// use the defined max angle of rotation instead of
						// the full 90 degrees for maximum velocity
						amount /= Constants.ACCEL_MAX_ANGLE_MAX_MOVEMENT;
					}
					level.hero.velocity.x = level.hero.terminalVelocity.x
							* amount;
				}
				// Execute auto-forward movement on non-desktop platform
				else if (Gdx.app.getType() != ApplicationType.Desktop) {
					level.hero.velocity.x = level.hero.terminalVelocity.x;
				}
			}
			// Hero Jump
			if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE))
				level.hero.setJumping(true);
		}
	}

	public boolean isGameOver() {
		return lives < 0;
	}

	private void backToMenu() {
		// switch to menu screen
		ScreenTransition transition = ScreenTransitionSlide.init(0.75f,
				ScreenTransitionSlide.DOWN, false, Interpolation.bounceOut);
		game.setScreen(new MenuScreen(game), transition);
	}

	private void initPhysics() {
		if (b2world != null)
			b2world.dispose();
		b2world = new World(new Vector2(0, 0), true);
	}

	@Override
	public void dispose() {
		if (b2world != null)
			b2world.dispose();
	}
}
