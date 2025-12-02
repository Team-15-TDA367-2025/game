package se.chalmers.tda367.team15.game.model.world;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

public class WorldMap {
    private final int width;
    private final int height;
    private final float tileSize;
    private final Tile[][] tiles;

    public WorldMap(int width, int height, float tileSize, TerrainGenerator generator) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.tiles = generator.generate(width, height);
    }

    public Tile getTile(int x, int y) {
        if (!isInBounds(x, y)) {
            return null;
        }
        return tiles[x][y];
    }

    public Tile getTile(GridPoint2 tilePos) {
        return getTile(tilePos.x, tilePos.y);
    }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean isInBounds(GridPoint2 tilePos) {
        return isInBounds(tilePos.x, tilePos.y);
    }

    public GridPoint2 worldToTile(Vector2 worldPos) {
        return new GridPoint2(
            (int) Math.floor(worldPos.x / tileSize) + width / 2,
            (int) Math.floor(worldPos.y / tileSize) + height / 2
        );
    }

    public Vector2 tileToWorld(GridPoint2 tilePos) {
        return new Vector2(
            (tilePos.x - width / 2f) * tileSize + tileSize / 2f,
            (tilePos.y - height / 2f) * tileSize + tileSize / 2f
        );
    }

    public GridPoint2 getSize() {
        return new GridPoint2(width, height);
    }

    public float getTileSize() {
        return tileSize;
    }

    /**
     * Get tiles in a rectangular region for efficient rendering.
     * Returns null for out-of-bounds tiles.
     * This method avoids repeated bounds checking in tight loops.
     */
    public Tile getTileUnchecked(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return null;
        }
        return tiles[x][y];
    }

    public Tile getTileUnchecked(GridPoint2 tilePos) {
        return getTileUnchecked(tilePos.x, tilePos.y);
    }
}
