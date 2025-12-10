package se.chalmers.tda367.team15.game.model.world.terrain.features;

import java.util.Random;

import se.chalmers.tda367.team15.game.model.world.terrain.TerrainFeature;
import se.chalmers.tda367.team15.game.model.world.terrain.TerrainGenerationContext;

/**
 * Generates random walk lakes and smooths them.
 */
public class LakeFeature implements TerrainFeature {
    
    public record Config(
        int lakeCount,
        int lakeMinSteps,
        int lakeMaxSteps,
        int lakeSmoothingPasses,
        int centerExclusionRadius
    ) {}

    private final Config config;

    public LakeFeature(Config config) {
        this.config = config;
    }

    @Override
    public void apply(TerrainGenerationContext context) {
        boolean[][] existingWater = context.getWaterMap();
        boolean[][] lakeMap = generateLakes(context, existingWater);
        boolean[][] smoothedLakes = smoothLakes(lakeMap);
        
        context.setWaterMap(smoothedLakes);
    }

    private boolean[][] generateLakes(TerrainGenerationContext context, boolean[][] existingWater) {
        int width = context.getWidth();
        int height = context.getHeight();
        boolean[][] lakeMap;
        
        if (existingWater != null) {
            lakeMap = copyBooleanMap(existingWater);
        } else {
            lakeMap = new boolean[width][height];
        }

        Random lakeRandom = new Random(context.getSeed() + 1000);

        int margin = Math.max(width, height) / 8;
        int centerX = width / 2;
        int centerY = height / 2;
        int centerExclusionRadius = config.centerExclusionRadius();

        for (int lake = 0; lake < config.lakeCount(); lake++) {
            int startX, startY;
            int attempts = 0;
            do {
                startX = margin + lakeRandom.nextInt(width - 2 * margin);
                startY = margin + lakeRandom.nextInt(height - 2 * margin);
                attempts++;
            } while (attempts < 50 && 
                     Math.sqrt((startX - centerX) * (startX - centerX) + 
                               (startY - centerY) * (startY - centerY)) < centerExclusionRadius);

            int steps = config.lakeMinSteps() + lakeRandom.nextInt(config.lakeMaxSteps() - config.lakeMinSteps());
            int x = startX;
            int y = startY;
            double angle = lakeRandom.nextDouble() * Math.PI * 2;
            
            for (int step = 0; step < steps; step++) {
                int blobRadius = 1 + lakeRandom.nextInt(2);
                paintBlob(lakeMap, x, y, blobRadius, width, height);

                angle += (lakeRandom.nextDouble() - 0.5) * 0.8;
                if (lakeRandom.nextDouble() < 0.15) {
                    angle += (lakeRandom.nextDouble() - 0.5) * Math.PI * 0.5;
                }

                int stepSize = 1 + lakeRandom.nextInt(2);
                x += (int) Math.round(Math.cos(angle) * stepSize);
                y += (int) Math.round(Math.sin(angle) * stepSize);

                x = Math.max(margin, Math.min(width - margin - 1, x));
                y = Math.max(margin, Math.min(height - margin - 1, y));

                double distToCenter = Math.sqrt((x - centerX) * (x - centerX) + 
                                                 (y - centerY) * (y - centerY));
                if (distToCenter < centerExclusionRadius) {
                    double pushAngle = Math.atan2(y - centerY, x - centerX);
                    x = centerX + (int) (Math.cos(pushAngle) * centerExclusionRadius);
                    y = centerY + (int) (Math.sin(pushAngle) * centerExclusionRadius);
                    angle = pushAngle;
                }
            }
        }
        return lakeMap;
    }

    private void paintBlob(boolean[][] map, int cx, int cy, int radius, int width, int height) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                if (dx * dx + dy * dy <= radius * radius) {
                    int nx = cx + dx;
                    int ny = cy + dy;
                    if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                        map[nx][ny] = true;
                    }
                }
            }
        }
    }

    private boolean[][] smoothLakes(boolean[][] lakeMap) {
        int width = lakeMap.length;
        int height = lakeMap[0].length;
        boolean[][] current = copyBooleanMap(lakeMap);

        for (int pass = 0; pass < config.lakeSmoothingPasses(); pass++) {
            boolean[][] next = new boolean[width][height];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int waterNeighbors = countWaterNeighbors(current, x, y, width, height);
                    if (current[x][y]) {
                        next[x][y] = waterNeighbors >= 2;
                    } else {
                        next[x][y] = waterNeighbors >= 6;
                    }
                }
            }
            current = next;
        }
        return current;
    }

    private int countWaterNeighbors(boolean[][] map, int x, int y, int width, int height) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < width && ny >= 0 && ny < height && map[nx][ny]) {
                    count++;
                }
            }
        }
        return count;
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
