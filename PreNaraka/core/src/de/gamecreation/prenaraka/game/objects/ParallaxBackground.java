package de.gamecreation.prenaraka.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class ParallaxBackground extends AbstractGameObject {
	private TextureRegion regBGLeft;
	private TextureRegion regBGRight;
	private int length;

	public ParallaxBackground(int length) {
		this.length = length;
		init();
	}

	private void init() {
		dimension.set(10, 2);
//		regBGLeft = Assets.instance.getObjectFromAssetManager("BackgraundLeft");
//		regBGRight = Assets.instance.getObjectFromAssetManager("BackgraundRight");
		// shift mountain and extend length
		origin.x = -dimension.x * 2;
		length += dimension.x * 2;
	}

	private void drawMountain(SpriteBatch batch, float offsetX, float offsetY,
			float tintColor, float parallaxSpeedX) {
		TextureRegion reg = null;
		batch.setColor(tintColor, tintColor, tintColor, 1);
		float xRel = dimension.x * offsetX;
		float yRel = dimension.y * offsetY;

		// mountains span the whole level
		int lengthBG = 0;
		lengthBG += MathUtils.ceil(length / (2 * dimension.x)
				* (1 - parallaxSpeedX));
		lengthBG += MathUtils.ceil(0.5f + offsetX);
		for (int i = 0; i < lengthBG; i++) {
			// Background left
			reg = regBGLeft;
			batch.draw(reg.getTexture(), origin.x + xRel + position.x
					* parallaxSpeedX, origin.y + yRel + position.y, origin.x,
					origin.y, dimension.x, dimension.y, scale.x, scale.y,
					rotation, reg.getRegionX(), reg.getRegionY(),
					reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			xRel += dimension.x;
			// Background right
			reg = regBGRight;
			batch.draw(reg.getTexture(), origin.x + xRel + position.x
					* parallaxSpeedX, origin.y + yRel + position.y, origin.x,
					origin.y, dimension.x, dimension.y, scale.x, scale.y,
					rotation, reg.getRegionX(), reg.getRegionY(),
					reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			xRel += dimension.x;
		}
		// reset color to white
		batch.setColor(1, 1, 1, 1);
	}

	@Override
	public void render(SpriteBatch batch) {
		// 80% distant Background (dark gray)
		drawMountain(batch, 0.5f, 0.5f, 0.5f, 0.8f);
		// 50% distant Background (gray)
		drawMountain(batch, 0.25f, 0.25f, 0.7f, 0.5f);
		// 30% distant Background (light gray)
		drawMountain(batch, 0.0f, 0.0f, 0.9f, 0.3f);
	}

	public void updateScrollPosition(Vector2 camPosition) {
		position.set(camPosition.x, position.y);
	}
}
