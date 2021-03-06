package de.gamecreation.prenaraka.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.gamecreation.prenaraka.util.CharacterSkin;
import de.gamecreation.prenaraka.util.GamePreferences;

public class Hero extends AbstractGameObject {
	public static final String TAG = Hero.class.getName();

	private final float JUMP_TIME_MAX = 0.3f;
	private final float JUMP_TIME_MIN = 0.1f;
	private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX - 0.018f;

	public enum VIEW_DIRECTION {
		LEFT, RIGHT
	}

	public enum JUMP_STATE {
		GROUNDED, FALLING, JUMP_RISING, JUMP_FALLING
	}

	private TextureRegion regHero;
	public VIEW_DIRECTION viewDirection;
	public float timeJumping;
	public JUMP_STATE jumpState;

	public ParticleEffect dustParticles = new ParticleEffect();

	// Animation
	private Animation animNormal;

	public Hero() {
		init();
	}

	public void init() {
		dimension.set(1, 1);

		// Center image on game object
		origin.set(dimension.x / 2, dimension.y / 2);
		// Bounding box for collision detection
		bounds.set(0, 0, dimension.x, dimension.y);
		// Set physics values
		terminalVelocity.set(3.0f, 4.0f);
		friction.set(12.0f, 0.0f);
		acceleration.set(0.0f, -25.0f);
		// View direction
		viewDirection = VIEW_DIRECTION.RIGHT;
		// Jump state
		jumpState = JUMP_STATE.FALLING;
		timeJumping = 0;
		// Particles
//		dustParticles.load(Assets.instance.getObjectFromAssetManager("particles"));
	};

	public void setJumping(boolean jumpKeyPressed) {
		switch (jumpState) {
		case GROUNDED: // Character is standing on a platform
			if (jumpKeyPressed) {
//				AudioManager.instance.play(Assets.instance.sounds.jump);
				// Start counting jump time from the beginning
				timeJumping = 0;
				jumpState = JUMP_STATE.JUMP_RISING;
			}
			break;
		case JUMP_RISING: // Rising in the air
			if (!jumpKeyPressed)
				jumpState = JUMP_STATE.JUMP_FALLING;
			break;
		case FALLING:// Falling down
		case JUMP_FALLING: // Falling down after jump
			break;
		}
	};

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);

		if (velocity.x != 0) {
			viewDirection = velocity.x < 0 ? VIEW_DIRECTION.LEFT
					: VIEW_DIRECTION.RIGHT;
		}
		
		dustParticles.update(deltaTime);
	}

	@Override
	protected void updateMotionY(float deltaTime) {
		switch (jumpState) {
		case GROUNDED:
			jumpState = JUMP_STATE.FALLING;
			if (velocity.x != 0) {
				dustParticles.setPosition(position.x + dimension.x / 2,
						position.y);
				dustParticles.start();
			}
			break;
		case JUMP_RISING:
			// Keep track of jump time
			timeJumping += deltaTime;
			// Jump time left?
			if (timeJumping <= JUMP_TIME_MAX) {
				// Still jumping
				velocity.y = terminalVelocity.y;
			}
			break;
		case FALLING:
			break;
		case JUMP_FALLING:
			// Add delta times to track jump time
			timeJumping += deltaTime;
			// Jump to minimal height if jump key was pressed too short
			if (timeJumping > 0 && timeJumping <= JUMP_TIME_MIN) {
				// Still jumping
				velocity.y = terminalVelocity.y;
			}
		}
		if (jumpState != JUMP_STATE.GROUNDED)
			dustParticles.allowCompletion();
		super.updateMotionY(deltaTime);
	}

	@Override
	public void render(SpriteBatch batch) {
		TextureRegion reg = null;

		// Draw Particles
		dustParticles.draw(batch);

		// Apply Skin Color
		batch.setColor(CharacterSkin.values()[GamePreferences.instance.charSkin]
				.getColor());

		// Animation
		float dimCorrectionX = 0;
		float dimCorrectionY = 0;
		if (animation != animNormal) {
			dimCorrectionX = 0.05f;
			dimCorrectionY = 0.2f;
		}

		// Draw image
		reg = animation.getKeyFrame(stateTime, true);
		batch.draw(reg.getTexture(), position.x, position.y, origin.x,
				origin.y, dimension.x + dimCorrectionX, dimension.y
						+ dimCorrectionY, scale.x, scale.y, rotation,
				reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(),
				reg.getRegionHeight(), viewDirection == VIEW_DIRECTION.LEFT,
				false);
		// Reset color to white
		batch.setColor(1, 1, 1, 1);
	}
	
}
