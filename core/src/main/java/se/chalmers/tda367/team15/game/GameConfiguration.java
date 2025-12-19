package se.chalmers.tda367.team15.game;

import com.badlogic.gdx.math.GridPoint2;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

public record GameConfiguration(boolean unlimitedFps, boolean noFog, int startAnts, Long seed,
        int startResources, String antType, GridPoint2 mapSize) {
    public static final float WORLD_VIEWPORT_WIDTH = 15f;
    public static final float MIN_ZOOM = 0.05f;
    public static final float MAX_ZOOM = 4.0f;
    public static final int TICKS_PER_MINUTE = 6;
    public static final int GRASS_VARIANT_TYPES = 3;

    @Command(name = "game", mixinStandardHelpOptions = true, description = "Game configuration")
    private static class CliArgs {
        @Option(names = "--unlimited-fps")
        boolean unlimitedFps = false;
        @Option(names = "--no-fog")
        boolean noFog = false;
        @Option(names = "--start-ants", defaultValue = "1")
        int startAnts;
        @Option(names = "--seed")
        Long seed;
        @Option(names = "--start-resources", defaultValue = "20")
        int startResources;
        @Option(names = "--ant-type", defaultValue = "worker")
        String antType;
        @Option(names = "--map-size", defaultValue = "400,400", converter = GridPointConverter.class)
        GridPoint2 mapSize;
    }

    public static GameConfiguration fromArgs(String[] args) {
        CliArgs cli = new CliArgs();
        new CommandLine(cli).parseArgs(args);

        return new GameConfiguration(
                cli.unlimitedFps,
                cli.noFog,
                cli.startAnts,
                cli.seed,
                cli.startResources,
                cli.antType,
                cli.mapSize);
    }

    private static class GridPointConverter implements CommandLine.ITypeConverter<GridPoint2> {
        @Override
        public GridPoint2 convert(String value) {
            String[] parts = value.split(",");
            return new GridPoint2(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
    }
}
