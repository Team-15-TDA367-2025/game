package se.chalmers.tda367.team15.game.model.world.terrain.features;

import se.chalmers.tda367.team15.game.model.world.PerlinNoise;
import se.chalmers.tda367.team15.game.model.world.TerrainGenerationConfig;
import se.chalmers.tda367.team15.game.model.world.terrain.TerrainFeature;
import se.chalmers.tda367.team15.game.model.world.terrain.TerrainGenerationContext;

/**
 * Generates a base height map using Perlin noise.
 */
public class PerlinHeightMapFeature implements TerrainFeature {
    private final TerrainGenerationConfig config;

    public PerlinHeightMapFeature(TerrainGenerationConfig config) {
        this.config = config;
    }

    @Override
    public void apply(TerrainGenerationContext context) {
        int width = context.getWidth();
        int height = context.getHeight();
        PerlinNoise perlinNoise = new PerlinNoise(context.getSeed());
        
        double[][] noiseMap = new double[width][height];
        double minNoise = Double.MAX_VALUE;
        double maxNoise = Double.MIN_VALUE;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double amplitude = 1.0;
                double frequency = 1.0;
                double noiseValue = 0.0;

                for (int o = 0; o < config.getOctaves(); o++) {
                    double sampleX = x * config.getScale() * frequency;
                    double sampleY = y * config.getScale() * frequency;
                    double perlinValue = perlinNoise.noise(sampleX, sampleY);
                    noiseValue += perlinValue * amplitude;
                    amplitude *= config.getPersistence();
                    frequency *= config.getLacunarity();
                }

                noiseMap[x][y] = noiseValue;
                minNoise = Math.min(minNoise, noiseValue);
                maxNoise = Math.max(maxNoise, noiseValue);
            }
        }

        // Normalize and apply redistribution
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double normalized = (noiseMap[x][y] - minNoise) / (maxNoise - minNoise);
                noiseMap[x][y] = Math.pow(normalized, config.getRedistribution());
            }
        }
        
        context.setHeightMap(noiseMap);
    }
}

