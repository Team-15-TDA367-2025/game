package se.chalmers.tda367.team15.game.model.world;

public interface TerrainGenerator {
    /**
     * Generates terrain tiles and returns the result with optional feature
     * locations.
     * 
     * @param width  width of the terrain grid
     * @param height height of the terrain grid
     * @return TerrainGenerationResult containing tiles and nucleation points
     */
    TerrainGenerationResult generate(int width, int height);
}
