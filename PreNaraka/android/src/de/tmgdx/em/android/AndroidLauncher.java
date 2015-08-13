package de.tmgdx.em.android;

import android.os.Bundle;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import de.gamecreation.prenaraka.Main;
import de.gamecreation.prenaraka.PlatformSpecificCode;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new Main(new DesktopSpecificCode()), config);
	}
}
class DesktopSpecificCode implements PlatformSpecificCode{
	private final ApplicationType appType = ApplicationType.Android;
	
	@Override
	public ApplicationType getPlatform() {
		return appType;
	}
}
