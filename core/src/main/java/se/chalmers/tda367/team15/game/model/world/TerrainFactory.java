package se.chalmers.tda367.team15.game.model.world;

import java.util.Arrays;
import java.util.List;

import se.chalmers.tda367.team15.game.model.world.terrain.PipelineTerrainGenerator;
import se.chalmers.tda367.team15.game.model.world.terrain.TerrainFeature;
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
    public static TerrainGenerator createStandardPerlinGenerator(long seed, TerrainGenerationConfig config) {
        List<TerrainFeature> pipeline = Arrays.asList(
            new PerlinHeightMapFeature(config),
            new LakeFeature(config),
            new TextureApplicationFeature(config),
            new ResourcePlacementFeature(config)
        );

        return new PipelineTerrainGenerator(seed, pipeline);
    }
}

