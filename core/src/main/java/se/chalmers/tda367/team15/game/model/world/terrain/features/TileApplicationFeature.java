package se.chalmers.tda367.team15.game.model.world.terrain.features;

import se.chalmers.tda367.team15.game.model.world.Tile;
import se.chalmers.tda367.team15.game.model.world.TileType;
import se.chalmers.tda367.team15.game.model.world.terrain.TerrainFeature;
import se.chalmers.tda367.team15.game.model.world.terrain.TerrainGenerationContext;

/**
 * Applies textures and tile types based on height and water maps.
 * Also handles sand borders.
 */
public class TileApplicationFeature implements TerrainFeature {
    @Override
    public void apply(TerrainGenerationContext context) {
        int width = context.getWidth();
        int height = context.getHeight();

        Tile[][] tileMap = new Tile[width][height];

        // 1. Initial Tile Assignment
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (context.isWater(x, y)) {
                    tileMap[x][y] = new Tile(0, TileType.WATER);
                } else {
                    int variant = getVariant(context, x, y);
                    tileMap[x][y] = new Tile(variant, TileType.GRASS);
                }
            }
        }

        // 2. Apply Sand Borders
        applySandBorders(tileMap, context.getWidth(), context.getHeight(), context);

        // 3. Set Tiles in context
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                context.setTile(x, y, tileMap[x][y]);
            }
        }
    }

    private int getVariant(TerrainGenerationContext context, int x, int y) {
        return (int) Math.floor(context.getHeight(x, y) * context.getVariantCount());
    }

    private void applySandBorders(Tile[][] tileMap, int width, int height, TerrainGenerationContext context) {
        boolean[][] waterMap = context.getWaterMap();
        if (waterMap == null)
            return;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Already water, skip
                if (waterMap[x][y])
                    continue;

                // If neighbor is water, set to sand
                if (hasWaterNeighbor(x, y, context)) {
                    tileMap[x][y] = new Tile(0, TileType.SAND);
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

}
