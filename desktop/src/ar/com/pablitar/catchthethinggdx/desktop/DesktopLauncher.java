package ar.com.pablitar.catchthethinggdx.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import ar.com.pablitar.catchthethinggdx.CatchTheThingGdxGame;
import ar.com.pablitar.catchthethinggdx.Configuration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Configuration.VIEWPORT_WIDTH();
		config.height = Configuration.VIEWPORT_HEIGHT();
		new LwjglApplication(new CatchTheThingGdxGame(), config);
	}
}
