package de.gamecreation.prenaraka.util;


public class Constants {
	//TODO delete unused Constants and configure the rest
	private static final String TAG = Constants.class.getName();
	// Visible game world is 5 meters wide
	public static final float VIEWPORT_WIDTH = 5.0f;
	// Visible game world is 5 meters tall
	public static final float VIEWPORT_HEIGHT = 5.0f;
	// GUI Width
	public static final float VIEWPORT_GUI_WIDTH = 800.0f;
	// GUI Height
	public static final float VIEWPORT_GUI_HEIGHT = 480.0f;
	// Amount of extra lives at level start
	public static final int LIVES_START = 3;
	// Delay after game over
		public static final float TIME_DELAY_GAME_OVER = 3;
		
	public static final String PREFERENCES = "preferencesFile";

	// Number of carrots to spawn
	public static final int CARROTS_SPAWN_MAX = 100;
	// Spawn radius for carrots
	public static final float CARROTS_SPAWN_RADIUS = 3.5f;
	// Delay after game finished
	public static final float TIME_DELAY_GAME_FINISHED = 6;

	// Accelerometer
	// Angle of rotation for dead zone (no movement)
	public static final float ACCEL_ANGLE_DEAD_ZONE = 5.0f;
	// Max angle of rotation needed to gain max movement velocity
	public static final float ACCEL_MAX_ANGLE_MAX_MOVEMENT = 20.0f;
}
