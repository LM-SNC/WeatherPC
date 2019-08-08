package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.Drop;
import com.mygdx.game.GameScreen;

public class DesktopLauncher {
	public static void main (String[] arg) {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "SNC_DrOP";
		config.height = 480;
		config.width = 800;
		new LwjglApplication(new Drop(), config);
	}
}
