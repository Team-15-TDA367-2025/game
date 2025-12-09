package se.chalmers.tda367.team15.game.model.world;

import java.util.List;
import java.util.Random;

// This is fully AI generated, just for rendering a bit nicer terrain!

/**
 * Terrain generator that uses Perlin noise to create natural-looking terrain.
 * The noise values are mapped to textures from a provided texture pool,
 * creating smooth transitions between terrain types.
 */
public class PerlinNoiseTerrainGenerator implements TerrainGenerator {
    private final List<String> texturePool;
    private final int[] permutation;
    private final double scale;
    private final int octaves;
    private final double persistence;
    private final double lacunarity;
    private final double redistribution;

    private static final int PERMUTATION_SIZE = 256;

    /**
     * Creates a Perlin noise terrain generator with a specific seed.
     *
     * @param texturePool the list of texture names to use
     * @param seed        the random seed for reproducible generation
     */
    public PerlinNoiseTerrainGenerator(List<String> texturePool, long seed) {
        this(texturePool, seed, 0.07, 4, 0.4, 2.0, 1.2);
    }

    /**
     * Creates a Perlin noise terrain generator with full control over parameters.
     *
     * @param texturePool    the list of texture names to use
     * @param seed           the random seed for reproducible generation
     * @param scale          controls the "zoom" level of the noise (smaller =
     *                       larger features)
     * @param octaves        number of noise layers to combine (more = more detail,
     *                       4-8 recommended)
     * @param persistence    amplitude multiplier per octave (0-1, higher = rougher
     *                       terrain)
     * @param lacunarity     frequency multiplier per octave (typically 2.0, higher
     *                       = more detail variation)
     * @param redistribution power curve for terrain distribution (1.0 = linear, >1
     *                       = more low areas, <1 = more high areas)
     */
    public PerlinNoiseTerrainGenerator(List<String> texturePool, long seed, double scale, int octaves,
            double persistence, double lacunarity, double redistribution) {
        this.texturePool = texturePool;
        this.scale = scale;
        this.octaves = octaves;
        this.persistence = persistence;
        this.lacunarity = lacunarity;
        this.redistribution = redistribution;
        this.permutation = generatePermutation(seed);
    }

    private int[] generatePermutation(long seed) {
        int[] perm = new int[PERMUTATION_SIZE * 2];
        int[] base = new int[PERMUTATION_SIZE];

        for (int i = 0; i < PERMUTATION_SIZE; i++) {
            base[i] = i;
        }

        // Fisher-Yates shuffle with seed
        Random random = new Random(seed);
        for (int i = PERMUTATION_SIZE - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = base[i];
            base[i] = base[j];
            base[j] = temp;
        }

        // Duplicate the permutation array
        for (int i = 0; i < PERMUTATION_SIZE; i++) {
            perm[i] = base[i];
            perm[PERMUTATION_SIZE + i] = base[i];
        }

        return perm;
    }

    @Override
    public Tile[][] generate(int width, int height) {
        Tile[][] tiles = new Tile[width][height];
        double[][] noiseMap = generateNoiseMap(width, height);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int textureIndex = mapNoiseToTextureIndex(noiseMap[x][y]);
                tiles[x][y] = new Tile(texturePool.get(textureIndex), TileType.GRASS); // TODO: set proper TileType
            }
        }

        return tiles;
    }

    private double[][] generateNoiseMap(int width, int height) {
        double[][] noiseMap = new double[width][height];
        double minNoise = Double.MAX_VALUE;
        double maxNoise = Double.MIN_VALUE;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double amplitude = 1.0;
                double frequency = 1.0;
                double noiseValue = 0.0;

                // Combine multiple octaves (fractal Brownian motion)
                for (int o = 0; o < octaves; o++) {
                    double sampleX = x * scale * frequency;
                    double sampleY = y * scale * frequency;

                    double perlinValue = perlin(sampleX, sampleY);
                    noiseValue += perlinValue * amplitude;

                    amplitude *= persistence; // Each octave contributes less
                    frequency *= lacunarity; // Each octave has higher frequency (more detail)
                }

                noiseMap[x][y] = noiseValue;
                minNoise = Math.min(minNoise, noiseValue);
                maxNoise = Math.max(maxNoise, noiseValue);
            }
        }

        // Normalize to 0-1 range and apply redistribution curve
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Normalize
                double normalized = (noiseMap[x][y] - minNoise) / (maxNoise - minNoise);

                // Apply redistribution curve for more natural terrain distribution
                // redistribution > 1 creates more lowlands, < 1 creates more highlands
                noiseMap[x][y] = Math.pow(normalized, redistribution);
            }
        }

        return noiseMap;
    }

    private double perlin(double x, double y) {
        // Find unit grid cell containing point
        int xi = (int) Math.floor(x) & 255;
        int yi = (int) Math.floor(y) & 255;

        // Get relative position within cell
        double xf = x - Math.floor(x);
        double yf = y - Math.floor(y);

        // Compute fade curves
        double u = fade(xf);
        double v = fade(yf);

        // Hash coordinates of the 4 corners
        int aa = permutation[permutation[xi] + yi];
        int ab = permutation[permutation[xi] + yi + 1];
        int ba = permutation[permutation[xi + 1] + yi];
        int bb = permutation[permutation[xi + 1] + yi + 1];

        // Blend results from 4 corners
        double x1 = lerp(grad(aa, xf, yf), grad(ba, xf - 1, yf), u);
        double x2 = lerp(grad(ab, xf, yf - 1), grad(bb, xf - 1, yf - 1), u);

        return lerp(x1, x2, v);
    }

    private double fade(double t) {
        // 6t^5 - 15t^4 + 10t^3 (improved Perlin fade function)
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    private double grad(int hash, double x, double y) {
        // Convert low 4 bits of hash into gradient direction
        int h = hash & 3;
        double u = h < 2 ? x : y;
        double v = h < 2 ? y : x;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    private int mapNoiseToTextureIndex(double noiseValue) {
        // Clamp to 0-1 range (should already be normalized, but just in case)
        noiseValue = Math.max(0, Math.min(1, noiseValue));

        // Map to texture index
        int index = (int) (noiseValue * texturePool.size());

        // Handle edge case where noiseValue is exactly 1.0
        if (index >= texturePool.size()) {
            index = texturePool.size() - 1;
        }

        return index;
    }
}
