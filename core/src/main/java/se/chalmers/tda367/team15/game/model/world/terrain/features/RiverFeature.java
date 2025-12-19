package se.chalmers.tda367.team15.game.model.world.terrain.features;

import java.util.Random;

import se.chalmers.tda367.team15.game.model.world.PerlinNoise;
import se.chalmers.tda367.team15.game.model.world.terrain.TerrainFeature;
import se.chalmers.tda367.team15.game.model.world.terrain.TerrainGenerationContext;

/**
 * Generates river-like water bodies using a random walk algorithm.
 */
public class RiverFeature implements TerrainFeature {

    private final int riverCount;
    private final int minLength;
    private final int maxLength;
    private final int centerExclusionRadius;
    private final double brushRadius;
    private final double widthVariance;

    public RiverFeature(int riverCount, int minLength, int maxLength, int centerExclusionRadius, double brushRadius,
            double widthVariance) {
        this.riverCount = riverCount;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.centerExclusionRadius = centerExclusionRadius;
        this.brushRadius = brushRadius;
        this.widthVariance = widthVariance;
    }

    @Override
    public void apply(TerrainGenerationContext context) {
        boolean[][] existingWater = context.getWaterMap();
        int width = context.getWidth();
        int height = context.getHeight();
        boolean[][] waterMap = (existingWater != null) ? copyBooleanMap(existingWater) : new boolean[width][height];

        generateRivers(context, waterMap);

        context.setWaterMap(waterMap);
    }

    private void generateRivers(TerrainGenerationContext context, boolean[][] waterMap) {
        Random random = new Random(context.getSeed() + 1000);
        PerlinNoise noise = new PerlinNoise(context.getSeed() + 1234);
        int width = context.getWidth();
        int height = context.getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int centerExclusionSq = centerExclusionRadius * centerExclusionRadius;

        for (int i = 0; i < riverCount; i++) {
            // 1. Pick a valid starting point
            int x, y;
            int attempts = 0;
            do {
                x = random.nextInt(width);
                y = random.nextInt(height);
                attempts++;
            } while (attempts < 50 &&
                    (x - centerX) * (x - centerX) + (y - centerY) * (y - centerY) < centerExclusionSq);

            // 2. Perform random walk
            int length = minLength + random.nextInt(maxLength - minLength + 1);

            double curX = x;
            double curY = y;
            double angle = random.nextDouble() * Math.PI * 2;

            // Random offset for noise sampling so rivers don't all look the same
            double noiseOffset = random.nextDouble() * 1000.0;

            for (int step = 0; step < length; step++) {
                // Calculate dynamic radius using Perlin noise
                // Use step as X coordinate for 1D noise
                double noiseVal = noise.noise(step * 0.1 + noiseOffset, 0);

                // Let's use the noise to modulate the base radius.
                // Configured radius +/- widthVariance
                double radiusMod = 1.0 + noiseVal * widthVariance;
                double currentRadius = brushRadius * radiusMod;
                currentRadius = Math.max(1.0, currentRadius); // Ensure at least 1px

                // Make sure the river doesn't cover the center exclusion radius
                if (Math.sqrt(Math.pow(curX - centerX, 2) + Math.pow(curY - centerY, 2)) < centerExclusionRadius) {
                    break;
                }

                // Mark current spot as water
                paintBrush(waterMap, (int) curX, (int) curY, currentRadius, width, height);

                // Update direction with random offset (wiggle)
                angle += (random.nextDouble() - 0.5) * 1.0;

                // Move forward
                curX += Math.cos(angle);
                curY += Math.sin(angle);
            }
        }
    }

    private void paintBrush(boolean[][] map, int x, int y, double radius, int width, int height) {
        int r = (int) Math.ceil(radius);
        double radiusSq = radius * radius;

        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                if (dx * dx + dy * dy <= radiusSq) {
                    int nx = x + dx;
                    int ny = y + dy;
                    if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                        map[nx][ny] = true;
                    }
                }
            }
        }
    }

    private boolean[][] copyBooleanMap(boolean[][] source) {
        int width = source.length;
        int height = source[0].length;
        boolean[][] copy = new boolean[width][height];
        for (int x = 0; x < width; x++) {
            System.arraycopy(source[x], 0, copy[x], 0, height);
        }
        return copy;
    }
}
