package de.tmgdx.em;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

import de.gamecreation.prenaraka.Main;
import de.gamecreation.prenaraka.PlatformSpecificCode;

public class IOSLauncher extends IOSApplication.Delegate {
	@Override
	protected IOSApplication createApplication() {
		IOSApplicationConfiguration config = new IOSApplicationConfiguration();
		return new IOSApplication(new Main(new IOSSpecificCode()), config);
	}

	public static void main(String[] argv) {
		NSAutoreleasePool pool = new NSAutoreleasePool();
		UIApplication.main(argv, null, IOSLauncher.class);
		pool.close();
	}
}

class IOSSpecificCode implements PlatformSpecificCode {
	private final ApplicationType appType = ApplicationType.iOS;

	@Override
	public ApplicationType getPlatform() {
		return appType;
	}
}