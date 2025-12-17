package se.chalmers.tda367.team15.game.model.world;

import java.util.Arrays;
import java.util.List;

import se.chalmers.tda367.team15.game.model.world.terrain.PipelineTerrainGenerator;
import se.chalmers.tda367.team15.game.model.world.terrain.TerrainFeature;
import se.chalmers.tda367.team15.game.model.world.terrain.features.IslandMaskFeature;
import se.chalmers.tda367.team15.game.model.world.terrain.features.RiverFeature;
import se.chalmers.tda367.team15.game.model.world.terrain.features.TextureApplicationFeature;
import se.chalmers.tda367.team15.game.model.world.terrain.features.PerlinHeightMapFeature;
import se.chalmers.tda367.team15.game.model.world.terrain.features.ResourcePlacementFeature;

/**
 * Factory for creating configured terrain generators.
 */
public class TerrainFactory {

    private TerrainFactory() {
        // Static factory
    }

    /**
     * Creates a standard Perlin noise-based terrain generator.
     * Includes base height map, lakes, textures/sand, and resource placement.
     */
    public static TerrainGenerator createStandardPerlinGenerator(long seed) {

        // Define default configurations
        List<TerrainFeature> pipeline = Arrays.asList(
                new PerlinHeightMapFeature(0.07, 4, 0.4, 2.0, 1.2),
                new IslandMaskFeature(0.5, 0.01, 0.1, 0.3),
                new RiverFeature(70, 50, 150, 20, 2.0, 2),
                new TextureApplicationFeature(),
                new ResourcePlacementFeature(100, 2, 20, 10, 1));

        return new PipelineTerrainGenerator(seed, pipeline);
    }
}
