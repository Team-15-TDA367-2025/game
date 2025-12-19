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

    private final double islandFactor;
    private final double deepWaterStartPercentage;
    private final double noiseScale;
    private final double noiseAmount;

    private static final double WATER_THRESHOLD = 0.2;

    /**
     * @param islandFactor             How aggressively to mask the edges.
     * @param deepWaterStartPercentage The percentage of the map at which the deep
     *                                 water starts.
     * @param noiseScale               Scale of the noise to break up the perfect
     *                                 circle.
     * @param noiseAmount              How much the noise affects the distance mask.
     */
    public IslandMaskFeature(double islandFactor, double deepWaterStartPercentage, double noiseScale,
            double noiseAmount) {
        this.islandFactor = islandFactor;
        this.deepWaterStartPercentage = deepWaterStartPercentage;
        this.noiseScale = noiseScale;
        this.noiseAmount = noiseAmount;
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
                double noise = noiseGen.noise(x * noiseScale, y * noiseScale);
                double distortedDist = normalizedDist + noise * noiseAmount;

                // Clamp to valid range for Math.pow
                distortedDist = Math.max(0.0, distortedDist);

                // Apply mask
                // As we get closer to the edge (normalizedDist -> 1.0), the mask value
                // increases

                double mask = Math.pow(distortedDist, islandFactor);

                // Invert the mask logic: gradient is 1.0 at center and 0.0 at edges.
                double gradient = 1.0 - mask;

                // Determine water based on gradient threshold
                if (gradient < WATER_THRESHOLD) {
                    waterMap[x][y] = true;
                }

                // Force very edges to be water
                if (x < width * deepWaterStartPercentage || x > width * (1 - deepWaterStartPercentage)
                        || y < height * deepWaterStartPercentage || y > height * (1 - deepWaterStartPercentage)) {
                    waterMap[x][y] = true;
                }
            }
        }

        context.setWaterMap(waterMap);
    }
}
