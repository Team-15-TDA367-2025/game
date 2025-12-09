package se.chalmers.tda367.team15.game.model.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.math.GridPoint2;

/**
 * Terrain generator that uses Perlin noise combined with random walk lakes
 * to create natural-looking terrain. Features:
 * - Base terrain from multi-octave Perlin noise
 * - Random walk lakes (serpentine, river-like) with smoothed edges
 * - Sand borders around all water bodies
 * - Nucleation points for resource spawning with grass1 surroundings
 * - Guaranteed resource spawn near colony (center)
 * - Proper TileType assignment based on texture
 */
public class PerlinNoiseTerrainGenerator implements TerrainGenerator {
    // Texture names (ordered by elevation: low to high)
    private static final String TEXTURE_WATER = "water";
    private static final String TEXTURE_SAND = "sand";
    private static final String[] TEXTURE_GRASS = { "grass1", "grass2", "grass3" };

    private final int[] permutation;
    private final long seed;
    private final Random random;

    // Noise parameters
    private final double scale;
    private final int octaves;
    private final double persistence;
    private final double lacunarity;
    private final double redistribution;

    // Lake parameters
    private final int lakeCount;
    private final int lakeMinSteps;
    private final int lakeMaxSteps;
    private final int lakeSmoothingPasses;

    // Nucleation parameters
    private final int nucleationCount;
    private final int nucleationRadius;
    private final int nucleationMinDistance;
    private final int colonyNucleationRadius;

    // Sand border width around water
    private final int sandBorderWidth;

    private static final int PERMUTATION_SIZE = 256;

    /**
     * Creates a Perlin noise terrain generator with default parameters.
     */
    public PerlinNoiseTerrainGenerator(long seed) {
        this(seed, new Builder());
    }

    /**
     * Creates a terrain generator with custom parameters via builder.
     */
    public PerlinNoiseTerrainGenerator(long seed, Builder config) {
        this.seed = seed;
        this.random = new Random(seed);
        this.permutation = generatePermutation(seed);

        // Noise parameters
        this.scale = config.scale;
        this.octaves = config.octaves;
        this.persistence = config.persistence;
        this.lacunarity = config.lacunarity;
        this.redistribution = config.redistribution;

        // Lake parameters
        this.lakeCount = config.lakeCount;
        this.lakeMinSteps = config.lakeMinSteps;
        this.lakeMaxSteps = config.lakeMaxSteps;
        this.lakeSmoothingPasses = config.lakeSmoothingPasses;

        // Nucleation parameters
        this.nucleationCount = config.nucleationCount;
        this.nucleationRadius = config.nucleationRadius;
        this.nucleationMinDistance = config.nucleationMinDistance;
        this.colonyNucleationRadius = config.colonyNucleationRadius;

        // Border parameters
        this.sandBorderWidth = config.sandBorderWidth;
    }

    /**
     * Builder for configuring terrain generation parameters.
     */
    public static class Builder {
        double scale = 0.07;
        int octaves = 4;
        double persistence = 0.4;
        double lacunarity = 2.0;
        double redistribution = 1.2;

        // More lakes, but thinner and more winding
        int lakeCount = 15;
        int lakeMinSteps = 80;
        int lakeMaxSteps = 100;
        int lakeSmoothingPasses = 3;

        int nucleationCount = 100;
        int nucleationRadius = 2;
        int nucleationMinDistance = 20;
        int colonyNucleationRadius = 10; // Spawn one resource within 10 tiles of center

        int sandBorderWidth = 1;

        public Builder scale(double scale) { this.scale = scale; return this; }
        public Builder octaves(int octaves) { this.octaves = octaves; return this; }
        public Builder persistence(double persistence) { this.persistence = persistence; return this; }
        public Builder lacunarity(double lacunarity) { this.lacunarity = lacunarity; return this; }
        public Builder redistribution(double redistribution) { this.redistribution = redistribution; return this; }

        public Builder lakeCount(int count) { this.lakeCount = count; return this; }
        public Builder lakeMinSteps(int steps) { this.lakeMinSteps = steps; return this; }
        public Builder lakeMaxSteps(int steps) { this.lakeMaxSteps = steps; return this; }
        public Builder lakeSmoothingPasses(int passes) { this.lakeSmoothingPasses = passes; return this; }

        public Builder nucleationCount(int count) { this.nucleationCount = count; return this; }
        public Builder nucleationRadius(int radius) { this.nucleationRadius = radius; return this; }
        public Builder nucleationMinDistance(int distance) { this.nucleationMinDistance = distance; return this; }
        public Builder colonyNucleationRadius(int radius) { this.colonyNucleationRadius = radius; return this; }

        public Builder sandBorderWidth(int width) { this.sandBorderWidth = width; return this; }
    }

    private int[] generatePermutation(long seed) {
        int[] perm = new int[PERMUTATION_SIZE * 2];
        int[] base = new int[PERMUTATION_SIZE];

        for (int i = 0; i < PERMUTATION_SIZE; i++) {
            base[i] = i;
        }

        Random rng = new Random(seed);
        for (int i = PERMUTATION_SIZE - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int temp = base[i];
            base[i] = base[j];
            base[j] = temp;
        }

        for (int i = 0; i < PERMUTATION_SIZE; i++) {
            perm[i] = base[i];
            perm[PERMUTATION_SIZE + i] = base[i];
        }

        return perm;
    }

    @Override
    public TerrainGenerationResult generateWithFeatures(int width, int height) {
        // Phase 1: Generate base noise map
        double[][] noiseMap = generateNoiseMap(width, height);

        // Phase 2: Generate random walk lakes (thin, serpentine)
        boolean[][] lakeMap = generateRandomWalkLakes(width, height);

        // Phase 3: Smooth lake edges using cellular automata
        boolean[][] smoothedLakes = smoothLakes(lakeMap, lakeSmoothingPasses);

        // Phase 4: Generate nucleation points (avoiding water, with one near center)
        List<GridPoint2> nucleationPoints = generateNucleationPoints(width, height, smoothedLakes);

        // Phase 5: Create initial texture map from noise
        String[][] textureMap = createTextureMap(width, height, noiseMap, smoothedLakes);

        // Phase 6: Apply nucleation zones (grass1 circles around nucleation points)
        applyNucleationZones(textureMap, nucleationPoints, smoothedLakes);

        // Phase 7: Add sand borders around water
        applySandBorders(textureMap, width, height);

        // Phase 8: Convert to tiles with proper TileTypes
        Tile[][] tiles = convertToTiles(textureMap, width, height);

        return new TerrainGenerationResult(tiles, nucleationPoints);
    }

    // ========== PHASE 1: Base Noise ==========

    private double[][] generateNoiseMap(int width, int height) {
        double[][] noiseMap = new double[width][height];
        double minNoise = Double.MAX_VALUE;
        double maxNoise = Double.MIN_VALUE;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double amplitude = 1.0;
                double frequency = 1.0;
                double noiseValue = 0.0;

                for (int o = 0; o < octaves; o++) {
                    double sampleX = x * scale * frequency;
                    double sampleY = y * scale * frequency;
                    double perlinValue = perlin(sampleX, sampleY);
                    noiseValue += perlinValue * amplitude;
                    amplitude *= persistence;
                    frequency *= lacunarity;
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
                noiseMap[x][y] = Math.pow(normalized, redistribution);
            }
        }

        return noiseMap;
    }

    private double perlin(double x, double y) {
        int xi = (int) Math.floor(x) & 255;
        int yi = (int) Math.floor(y) & 255;

        double xf = x - Math.floor(x);
        double yf = y - Math.floor(y);

        double u = fade(xf);
        double v = fade(yf);

        int aa = permutation[permutation[xi] + yi];
        int ab = permutation[permutation[xi] + yi + 1];
        int ba = permutation[permutation[xi + 1] + yi];
        int bb = permutation[permutation[xi + 1] + yi + 1];

        double x1 = lerp(grad(aa, xf, yf), grad(ba, xf - 1, yf), u);
        double x2 = lerp(grad(ab, xf, yf - 1), grad(bb, xf - 1, yf - 1), u);

        return lerp(x1, x2, v);
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    private double grad(int hash, double x, double y) {
        int h = hash & 3;
        double u = h < 2 ? x : y;
        double v = h < 2 ? y : x;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    // ========== PHASE 2: Random Walk Lakes (Thin & Serpentine) ==========

    private boolean[][] generateRandomWalkLakes(int width, int height) {
        boolean[][] lakeMap = new boolean[width][height];
        Random lakeRandom = new Random(seed + 1000);

        int margin = Math.max(width, height) / 8;
        int centerX = width / 2;
        int centerY = height / 2;
        int centerExclusionRadius = colonyNucleationRadius + 15; // Keep lakes away from colony

        for (int lake = 0; lake < lakeCount; lake++) {
            // Random starting point (avoiding center)
            int startX, startY;
            int attempts = 0;
            do {
                startX = margin + lakeRandom.nextInt(width - 2 * margin);
                startY = margin + lakeRandom.nextInt(height - 2 * margin);
                attempts++;
            } while (attempts < 50 && 
                     Math.sqrt((startX - centerX) * (startX - centerX) + 
                               (startY - centerY) * (startY - centerY)) < centerExclusionRadius);

            int steps = lakeMinSteps + lakeRandom.nextInt(lakeMaxSteps - lakeMinSteps);

            int x = startX;
            int y = startY;

            // Initial direction - will change frequently for serpentine shape
            double angle = lakeRandom.nextDouble() * Math.PI * 2;
            
            for (int step = 0; step < steps; step++) {
                // Paint a thin blob at current position (radius 1-2 for thin streams)
                int blobRadius = 1 + lakeRandom.nextInt(2);
                paintBlob(lakeMap, x, y, blobRadius, width, height);

                // Serpentine movement: frequently change direction with smooth curves
                // High frequency direction changes create winding paths
                angle += (lakeRandom.nextDouble() - 0.5) * 0.8; // Smooth curves
                
                // Occasionally make sharper turns
                if (lakeRandom.nextDouble() < 0.15) {
                    angle += (lakeRandom.nextDouble() - 0.5) * Math.PI * 0.5;
                }

                // Move in current direction
                int stepSize = 1 + lakeRandom.nextInt(2);
                x += (int) Math.round(Math.cos(angle) * stepSize);
                y += (int) Math.round(Math.sin(angle) * stepSize);

                // Keep in bounds and away from center
                x = Math.max(margin, Math.min(width - margin - 1, x));
                y = Math.max(margin, Math.min(height - margin - 1, y));

                // If we wandered into center exclusion zone, push back out
                double distToCenter = Math.sqrt((x - centerX) * (x - centerX) + 
                                                 (y - centerY) * (y - centerY));
                if (distToCenter < centerExclusionRadius) {
                    // Push away from center
                    double pushAngle = Math.atan2(y - centerY, x - centerX);
                    x = centerX + (int) (Math.cos(pushAngle) * centerExclusionRadius);
                    y = centerY + (int) (Math.sin(pushAngle) * centerExclusionRadius);
                    angle = pushAngle; // Continue moving away
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

    // ========== PHASE 3: Smooth Lake Edges ==========

    private boolean[][] smoothLakes(boolean[][] lakeMap, int passes) {
        int width = lakeMap.length;
        int height = lakeMap[0].length;
        boolean[][] current = copyBooleanMap(lakeMap);

        for (int pass = 0; pass < passes; pass++) {
            boolean[][] next = new boolean[width][height];

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int waterNeighbors = countWaterNeighbors(current, x, y, width, height);

                    // Smoothing rules tuned for thin streams
                    if (current[x][y]) {
                        // Water stays if it has at least 2 water neighbors
                        next[x][y] = waterNeighbors >= 2;
                    } else {
                        // Land becomes water only if heavily surrounded
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

    // ========== PHASE 4: Nucleation Points ==========

    private List<GridPoint2> generateNucleationPoints(int width, int height, boolean[][] waterMap) {
        List<GridPoint2> points = new ArrayList<>();
        Random nucRandom = new Random(seed + 2000);

        int centerX = width / 2;
        int centerY = height / 2;

        // First: Always spawn one nucleation point near the colony (center)
        GridPoint2 colonyNucleation = generateColonyNucleationPoint(
                centerX, centerY, waterMap, width, height, nucRandom);
        if (colonyNucleation != null) {
            points.add(colonyNucleation);
        }

        // Then: Generate remaining nucleation points away from center
        int margin = nucleationMinDistance;
        int attempts = 0;
        int maxAttempts = nucleationCount * 100;

        while (points.size() < nucleationCount && attempts < maxAttempts) {
            attempts++;

            int x = margin + nucRandom.nextInt(width - 2 * margin);
            int y = margin + nucRandom.nextInt(height - 2 * margin);

            // Check if position is valid (not in water and not too close to water)
            if (!isValidNucleationPoint(x, y, waterMap, width, height)) {
                continue;
            }

            // Check distance from other nucleation points
            boolean tooClose = false;
            for (GridPoint2 existing : points) {
                double dist = Math.sqrt((x - existing.x) * (x - existing.x) + 
                                        (y - existing.y) * (y - existing.y));
                if (dist < nucleationMinDistance) {
                    tooClose = true;
                    break;
                }
            }

            if (!tooClose) {
                points.add(new GridPoint2(x, y));
            }
        }

        return points;
    }

    /**
     * Generates a nucleation point within colonyNucleationRadius tiles of the center.
     */
    private GridPoint2 generateColonyNucleationPoint(int centerX, int centerY, 
            boolean[][] waterMap, int width, int height, Random rng) {
        
        for (int attempt = 0; attempt < 100; attempt++) {
            // Random angle and distance within the radius
            double angle = rng.nextDouble() * Math.PI * 2;
            double distance = 3 + rng.nextDouble() * (colonyNucleationRadius - 3); // At least 3 tiles away
            
            int x = centerX + (int) Math.round(Math.cos(angle) * distance);
            int y = centerY + (int) Math.round(Math.sin(angle) * distance);

            // Validate position
            if (x >= 0 && x < width && y >= 0 && y < height) {
                if (isValidNucleationPoint(x, y, waterMap, width, height)) {
                    return new GridPoint2(x, y);
                }
            }
        }

        // Fallback: just place at center if everything else fails
        return new GridPoint2(centerX + 5, centerY + 5);
    }

    private boolean isValidNucleationPoint(int x, int y, boolean[][] waterMap, int width, int height) {
        int checkRadius = nucleationRadius + sandBorderWidth + 2;

        for (int dx = -checkRadius; dx <= checkRadius; dx++) {
            for (int dy = -checkRadius; dy <= checkRadius; dy++) {
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    if (waterMap[nx][ny]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // ========== PHASE 5: Create Texture Map ==========

    private String[][] createTextureMap(int width, int height, double[][] noiseMap, boolean[][] waterMap) {
        String[][] textureMap = new String[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (waterMap[x][y]) {
                    textureMap[x][y] = TEXTURE_WATER;
                } else {
                    double noise = noiseMap[x][y];
                    textureMap[x][y] = noiseToGrassTexture(noise);
                }
            }
        }

        return textureMap;
    }

    private String noiseToGrassTexture(double noise) {
        int index = (int) (noise * TEXTURE_GRASS.length);
        return TEXTURE_GRASS[Math.min(index, TEXTURE_GRASS.length - 1)];
    }

    // ========== PHASE 6: Apply Nucleation Zones ==========

    private void applyNucleationZones(String[][] textureMap, List<GridPoint2> nucleationPoints, boolean[][] waterMap) {
        int width = textureMap.length;
        int height = textureMap[0].length;

        for (GridPoint2 point : nucleationPoints) {
            int smoothRadius = nucleationRadius + 3;

            for (int dx = -smoothRadius; dx <= smoothRadius; dx++) {
                for (int dy = -smoothRadius; dy <= smoothRadius; dy++) {
                    int nx = point.x + dx;
                    int ny = point.y + dy;

                    if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue;
                    if (waterMap[nx][ny]) continue;

                    double dist = Math.sqrt(dx * dx + dy * dy);

                    if (dist <= nucleationRadius) {
                        textureMap[nx][ny] = TEXTURE_GRASS[0];
                    } else if (dist <= smoothRadius) {
                        double blendFactor = (dist - nucleationRadius) / (smoothRadius - nucleationRadius);
                        if (random.nextDouble() > blendFactor * blendFactor) {
                            textureMap[nx][ny] = TEXTURE_GRASS[0];
                        }
                    }
                }
            }
        }
    }

    // ========== PHASE 7: Sand Borders ==========

    private void applySandBorders(String[][] textureMap, int width, int height) {
        Set<Long> waterTiles = new HashSet<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (TEXTURE_WATER.equals(textureMap[x][y])) {
                    waterTiles.add(packCoord(x, y));
                }
            }
        }

        Set<Long> sandTiles = new HashSet<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!TEXTURE_WATER.equals(textureMap[x][y])) {
                    if (isNearWater(x, y, waterTiles, sandBorderWidth, width, height)) {
                        sandTiles.add(packCoord(x, y));
                    }
                }
            }
        }

        for (Long packed : sandTiles) {
            int x = (int) (packed >> 32);
            int y = (int) (packed & 0xFFFFFFFFL);
            textureMap[x][y] = TEXTURE_SAND;
        }
    }

    private boolean isNearWater(int x, int y, Set<Long> waterTiles, int radius, int width, int height) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    if (waterTiles.contains(packCoord(nx, ny))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private long packCoord(int x, int y) {
        return ((long) x << 32) | (y & 0xFFFFFFFFL);
    }

    // ========== PHASE 8: Convert to Tiles ==========

    private Tile[][] convertToTiles(String[][] textureMap, int width, int height) {
        Tile[][] tiles = new Tile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                String texture = textureMap[x][y];
                TileType type = textureToTileType(texture);
                tiles[x][y] = new Tile(texture, type);
            }
        }

        return tiles;
    }

    private TileType textureToTileType(String texture) {
        switch (texture) {
            case TEXTURE_WATER:
                return TileType.WATER;
            case TEXTURE_SAND:
                return TileType.SAND;
            default:
                return TileType.GRASS;
        }
    }
}
