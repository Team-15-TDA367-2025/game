package se.chalmers.tda367.team15.game.model.world.terrain.features;

import com.badlogic.gdx.math.GridPoint2;

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
        applySandBorders(textureMap, context.getWidth(), context.getHeight(), context);

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

    private void applySandBorders(String[][] textureMap, int width, int height, TerrainGenerationContext context) {
        boolean[][] waterMap = context.getWaterMap();
        if (waterMap == null)
            return;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // If it's already water, skip
                if (waterMap[x][y])
                    continue;

                // Check neighbors for water
                if (hasWaterNeighbor(x, y, context)) {
                    textureMap[x][y] = TEXTURE_SAND;
                }
            }
        }
    }

    private boolean hasWaterNeighbor(int x, int y, TerrainGenerationContext context) {
        int[][] NEIGHBORS = { { -1, 0 }, { 0, -1 }, { 0, 1 }, { 1, 0 }, };
        for (int[] n : NEIGHBORS) {
            int nx = x + n[0];
            int ny = y + n[1];
            if (!context.isInBounds(nx, ny))
                continue;
            if (context.isWater(nx, ny))
                return true;
        }
        return false;
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
