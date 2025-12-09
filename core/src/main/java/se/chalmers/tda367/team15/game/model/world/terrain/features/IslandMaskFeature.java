package se.chalmers.tda367.team15.game.model.world.terrain.features;

import se.chalmers.tda367.team15.game.model.world.terrain.TerrainFeature;
import se.chalmers.tda367.team15.game.model.world.terrain.TerrainGenerationContext;

/**
 * Applies a radial mask to the height map to force the terrain into an island
 * shape.
 * Areas near the edge of the map are forced to be lower (water).
 */
public class IslandMaskFeature implements TerrainFeature {
    public record Config(
            /**
             * How aggressively to mask the edges.
             * 1.0 is a circular gradient. Higher values (e.g. 2.0) make
             * a steeper drop-off (squarer island).
             * Lower values (e.g. 0.5) make a very gentle slope from
             * center.
             */
            double islandFactor,
            /**
             * The percentage of the map at which the deep water starts.
             * 0.05 means 5% of the map.
             */
            double deepWaterStartPercentage) {
    }

    private final Config config;

    public IslandMaskFeature(Config config) {
        this.config = config;
    }

    @Override
    public void apply(TerrainGenerationContext context) {
        int width = context.getWidth();
        int height = context.getHeight();
        double[][] heightMap = context.getHeightMap();

        int centerX = width / 2;
        int centerY = height / 2;
        double maxDist = Math.sqrt(centerX * centerX + centerY * centerY);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Calculate normalized distance from center (0.0 at center, 1.0 at corners)
                double dx = x - centerX;
                double dy = y - centerY;
                double dist = Math.sqrt(dx * dx + dy * dy);
                double normalizedDist = dist / maxDist;

                // Apply mask
                // As we get closer to the edge (normalizedDist -> 1.0), the mask value
                // increases
                // We subtract this mask from the height map to lower the terrain at edges

                // Using a polynomial curve for the mask allows the center to stay intact
                // while the edges drop off sharply.
                // Formula: mask = dist ^ factor

                // We want the edges to be definitely water.
                // If we assume water level is e.g. < 0.3 (handled by
                // TextureApplicationFeature),
                // we need to ensure heightMap[x][y] becomes < 0.3 at the edges.

                double mask = Math.pow(normalizedDist, config.islandFactor());

                // Invert the mask logic: We want to multiply the existing height by a gradient
                // that is 1.0 at center and 0.0 at edges.
                // gradient = 1.0 - mask
                double gradient = 1.0 - mask;

                // Clamp gradient to 0
                if (gradient < 0)
                    gradient = 0;

                // Apply gradient to existing height
                heightMap[x][y] *= gradient;

                // Optional: Force very edges to be 0 (deep water)
                // If we are in the outer 5% of the map, force 0
                if (x < width * config.deepWaterStartPercentage() || x > width * (1 - config.deepWaterStartPercentage()) || y < height * config.deepWaterStartPercentage() || y > height * (1 - config.deepWaterStartPercentage())) {
                    heightMap[x][y] *= 0.5; // Reduce even further
                }
            }
        }
    }
}
