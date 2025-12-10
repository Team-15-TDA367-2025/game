package se.chalmers.tda367.team15.game.model.world.terrain.features;

import java.util.HashSet;
import java.util.Set;

import se.chalmers.tda367.team15.game.model.world.Tile;
import se.chalmers.tda367.team15.game.model.world.TileType;
import se.chalmers.tda367.team15.game.model.world.terrain.TerrainFeature;
import se.chalmers.tda367.team15.game.model.world.terrain.TerrainGenerationContext;

/**
 * Applies textures and tile types based on height and water maps.
 * Also handles sand borders.
 */
public class TextureApplicationFeature implements TerrainFeature {
    private static final String TEXTURE_WATER = "water";
    private static final String TEXTURE_SAND = "sand";
    private static final String[] TEXTURE_GRASS = { "grass1", "grass2", "grass3" };

    public record Config(
        int sandBorderWidth
    ) {}

    private final Config config;

    public TextureApplicationFeature(Config config) {
        this.config = config;
    }

    @Override
    public void apply(TerrainGenerationContext context) {
        int width = context.getWidth();
        int height = context.getHeight();
        String[][] textureMap = new String[width][height];

        // 1. Initial Texture Assignment
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double noise = context.getHeight(x, y);
                // Only treat explicitly marked areas as water (from IslandMask or LakeFeature)
                if (context.isWater(x, y)) {
                    textureMap[x][y] = TEXTURE_WATER;
                } else {
                    textureMap[x][y] = noiseToGrassTexture(noise);
                }
            }
        }

        // 2. Apply Sand Borders
        applySandBorders(textureMap, context.getWidth(), context.getHeight());

        // 3. Convert to Tiles
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                String texture = textureMap[x][y];
                TileType type = textureToTileType(texture);
                context.setTile(x, y, new Tile(texture, type));
            }
        }
    }

    private String noiseToGrassTexture(double noise) {
        int index = (int) (noise * TEXTURE_GRASS.length);
        return TEXTURE_GRASS[Math.min(index, TEXTURE_GRASS.length - 1)];
    }

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
                    if (isNearWater(x, y, waterTiles, config.sandBorderWidth(), width, height)) {
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
