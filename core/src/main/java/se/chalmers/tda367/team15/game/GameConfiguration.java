package se.chalmers.tda367.team15.game;

import java.util.Set;

import com.badlogic.gdx.math.GridPoint2;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.trail.ExploreTrailStrategy;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.trail.GatherTrailStrategy;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.trail.PatrolTrailStrategy;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;

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

    public static void registerAntTypes(AntTypeRegistry registry) {
        // Scout: High speed, low HP, 0 capacity, cheap/fast to hatch
        registry.register(AntType.with()
                .id("scout")
                .displayName("Scout")
                .foodCost(5)
                .developmentTicks(30)
                .maxHealth(4f)
                .moveSpeed(8f)
                .carryCapacity(0)
                .allowedPheromones(Set.of(PheromoneType.EXPLORE))
                .homeBias(0.05f) // Low home bias - scouts wander far
                .visionRadius(8)
                .hunger(2)
                .trailStrategy(new ExploreTrailStrategy())
                .build());

        // Soldier: Low speed, high HP, 0 capacity, expensive
        registry.register(AntType.with()
                .id("soldier")
                .displayName("Soldier")
                .foodCost(40)
                .developmentTicks(300)
                .maxHealth(20f)
                .moveSpeed(2f)
                .carryCapacity(50)
                .allowedPheromones(Set.of(PheromoneType.ATTACK))
                .homeBias(0.3f)
                .visionRadius(8)
                .hunger(2)
                .trailStrategy(new PatrolTrailStrategy())
                .build());

        // Worker: Medium speed, medium HP, some capacity
        registry.register(AntType.with()
                .id("worker")
                .displayName("Worker")
                .foodCost(10)
                .developmentTicks(60)
                .maxHealth(6f)
                .moveSpeed(5f)
                .carryCapacity(10)
                .allowedPheromones(Set.of(PheromoneType.GATHER))
                .homeBias(0.1f)
                .visionRadius(8)
                .hunger(2)
                .trailStrategy(new GatherTrailStrategy())
                .build());
    }
}
