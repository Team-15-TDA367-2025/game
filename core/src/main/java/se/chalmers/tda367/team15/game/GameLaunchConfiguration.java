package se.chalmers.tda367.team15.game;

public record GameLaunchConfiguration(boolean unlimitedFps, boolean noFog) {

    private static GameLaunchConfiguration current;

    public GameLaunchConfiguration(boolean unlimitedFps, boolean noFog) {
        this.unlimitedFps = unlimitedFps;
        this.noFog = noFog;
    }

    public static void setCurrent(GameLaunchConfiguration gameLaunchConfiguration) {
        current = gameLaunchConfiguration;
    }

    public static GameLaunchConfiguration getCurrent() {
        return current;
    }
}
