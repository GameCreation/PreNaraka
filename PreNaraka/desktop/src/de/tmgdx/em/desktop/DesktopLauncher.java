package de.tmgdx.em.desktop;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.gamecreation.prenaraka.Main;
import de.gamecreation.prenaraka.PlatformSpecificCode;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new Main(new DesktopSpecificCode()), config);
//		new LwjglApplication(new ExampleWrapper(), config); //TEST
	}
}

class DesktopSpecificCode implements PlatformSpecificCode{
	private final ApplicationType appType = ApplicationType.Desktop;
	
	@Override
	public ApplicationType getPlatform() {
		return appType;
	}
}

