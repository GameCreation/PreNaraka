package de.tmgdx.em.client;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import de.gamecreation.prenaraka.Main;
import de.gamecreation.prenaraka.PlatformSpecificCode;

public class HtmlLauncher extends GwtApplication {

	@Override
	public GwtApplicationConfiguration getConfig() {
		return new GwtApplicationConfiguration(480, 320);
	}

	@Override
	public ApplicationListener getApplicationListener() {
		 return new Main(new HtmlSpecificCode());

	}
}

class HtmlSpecificCode implements PlatformSpecificCode {
	private final ApplicationType appType = ApplicationType.WebGL;
	
	@Override
	public ApplicationType getPlatform() {
		return appType;
	}
	
} 