package se.chalmers.tda367.team15.game.model.world.terrain;

/**
 * Represents a single step in the terrain generation pipeline.
 */
public interface TerrainFeature {
    /**
     * Applies this feature to the terrain generation context.
     * 
     * @param context The current generation context.
     */
    void apply(TerrainGenerationContext context);
}
