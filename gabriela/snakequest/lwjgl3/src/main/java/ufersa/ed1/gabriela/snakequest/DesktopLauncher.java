package ufersa.ed1.gabriela.snakequest;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("SnakeQuest");
        config.setWindowedMode(25*24, 20*24);
        config.useVsync(true);
        config.setResizable(false);
        config.setWindowIcon("icon.png");
        new Lwjgl3Application(new SnakeGame(), config);
    }
}
