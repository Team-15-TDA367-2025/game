package se.chalmers.tda367.team15.game.model.world;

import java.util.Arrays;
import java.util.List;

import se.chalmers.tda367.team15.game.model.world.terrain.PipelineTerrainGenerator;
import se.chalmers.tda367.team15.game.model.world.terrain.TerrainFeature;
import se.chalmers.tda367.team15.game.model.world.terrain.features.IslandMaskFeature;
import se.chalmers.tda367.team15.game.model.world.terrain.features.LakeFeature;
import se.chalmers.tda367.team15.game.model.world.terrain.features.PerlinHeightMapFeature;
import se.chalmers.tda367.team15.game.model.world.terrain.features.ResourcePlacementFeature;
import se.chalmers.tda367.team15.game.model.world.terrain.features.TextureApplicationFeature;

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
            new PerlinHeightMapFeature(new PerlinHeightMapFeature.Config(0.07, 4, 0.4, 2.0, 1.2)),
            new IslandMaskFeature(new IslandMaskFeature.Config(0.5, 0.01)),
            new LakeFeature(new LakeFeature.Config(15, 80, 100, 100, 25)),
            new TextureApplicationFeature(new TextureApplicationFeature.Config(1)),
            new ResourcePlacementFeature(new ResourcePlacementFeature.Config(100, 2, 20, 10, 1))
        );

        return new PipelineTerrainGenerator(seed, pipeline);
    }
}
