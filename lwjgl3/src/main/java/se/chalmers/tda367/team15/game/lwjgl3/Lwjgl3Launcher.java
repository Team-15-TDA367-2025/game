package se.chalmers.tda367.team15.game.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import se.chalmers.tda367.team15.game.GameConfiguration;
import se.chalmers.tda367.team15.game.Main;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired())
            return; // This handles macOS support and helps on Windows.

        // Parse launch arguments
        GameConfiguration gameConfiguration = GameConfiguration.fromArgs(args);

        createApplication(gameConfiguration);
    }

    private static Lwjgl3Application createApplication(GameConfiguration gameConfiguration) {
        return new Lwjgl3Application(new Main(gameConfiguration), getDefaultConfiguration(gameConfiguration));
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration(GameConfiguration gameConfiguration) {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Game");

        if (gameConfiguration.unlimitedFps()) {
            // Unlimited FPS mode - disable vsync and set no FPS cap
            configuration.useVsync(false);
            configuration.setForegroundFPS(0);
        } else {
            // Default: Vsync enabled with FPS limited to monitor refresh rate
            configuration.useVsync(true);
            configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        }

        configuration.setWindowedMode(1280, 720);
        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        //// They can also be loaded from the root of assets/ .
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        //// This should improve compatibility with Windows machines with buggy OpenGL
        //// drivers, Macs
        //// with Apple Silicon that have to emulate compatibility with OpenGL anyway,
        //// and more.
        //// This uses the dependency `com.badlogicgames.gdx:gdx-lwjgl3-angle` to
        //// function.
        //// You can choose to remove the following line and the mentioned dependency if
        //// you want; they
        //// are not intended for games that use GL30 (which is compatibility with
        //// OpenGL ES 3.0).
        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.ANGLE_GLES20, 0, 0);

        return configuration;
    }
}