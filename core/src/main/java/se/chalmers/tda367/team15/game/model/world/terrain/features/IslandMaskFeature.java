package se.chalmers.tda367.team15.game.model.world.terrain.features;

import se.chalmers.tda367.team15.game.model.world.PerlinNoise;
import se.chalmers.tda367.team15.game.model.world.terrain.TerrainFeature;
import se.chalmers.tda367.team15.game.model.world.terrain.TerrainGenerationContext;

/**
 * Applies a radial mask to determine the island shape.
 * Areas near the edge of the map are forced to be water.
 * This does NOT modify the height map, only the water map.
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
            double deepWaterStartPercentage,
            /**
             * Scale of the noise to break up the perfect circle.
             * Lower values (e.g. 0.02) mean larger features.
             */
            double noiseScale,
            /**
             * How much the noise affects the distance mask.
             * 0.0 means perfect circle, 0.2 means significant distortion.
             */
            double noiseAmount) {
    }

    private final Config config;
    private static final double WATER_THRESHOLD = 0.2;

    public IslandMaskFeature(Config config) {
        this.config = config;
    }

    @Override
    public void apply(TerrainGenerationContext context) {
        int width = context.getWidth();
        int height = context.getHeight();
        
        PerlinNoise noiseGen = new PerlinNoise(context.getSeed());
        
        boolean[][] waterMap = context.getWaterMap();
        if (waterMap == null) {
            waterMap = new boolean[width][height];
        }

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
                
                // Add noise to the distance to break up the circle
                double noise = noiseGen.noise(x * config.noiseScale(), y * config.noiseScale());
                double distortedDist = normalizedDist + noise * config.noiseAmount();
                
                // Clamp to valid range for Math.pow
                distortedDist = Math.max(0.0, distortedDist);

                // Apply mask
                // As we get closer to the edge (normalizedDist -> 1.0), the mask value
                // increases
                
                double mask = Math.pow(distortedDist, config.islandFactor());

                // Invert the mask logic: gradient is 1.0 at center and 0.0 at edges.
                double gradient = 1.0 - mask;

                // Determine water based on gradient threshold
                if (gradient < WATER_THRESHOLD) {
                    waterMap[x][y] = true;
                }

                // Force very edges to be water
                if (x < width * config.deepWaterStartPercentage() || x > width * (1 - config.deepWaterStartPercentage()) || y < height * config.deepWaterStartPercentage() || y > height * (1 - config.deepWaterStartPercentage())) {
                    waterMap[x][y] = true;
                }
            }
        }
        
        context.setWaterMap(waterMap);
    }
}
