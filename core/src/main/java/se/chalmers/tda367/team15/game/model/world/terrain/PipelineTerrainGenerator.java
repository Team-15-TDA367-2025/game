package se.chalmers.tda367.team15.game.model.world.terrain;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.tda367.team15.game.model.world.TerrainGenerationResult;
import se.chalmers.tda367.team15.game.model.world.TerrainGenerator;

/**
 * A terrain generator that runs a pipeline of independent features.
 */
public class PipelineTerrainGenerator implements TerrainGenerator {
    private final List<TerrainFeature> pipeline;
    private final long seed;

    public PipelineTerrainGenerator(long seed, List<TerrainFeature> pipeline) {
        this.seed = seed;
        this.pipeline = new ArrayList<>(pipeline);
    }

    @Override
    public TerrainGenerationResult generate(int width, int height) {
        TerrainGenerationContext context = new TerrainGenerationContext(width, height, seed);

        for (TerrainFeature feature : pipeline) {
            feature.apply(context);
        }

        return new TerrainGenerationResult(context.getTileMap(), context.getStructureSpawns());
    }
}

