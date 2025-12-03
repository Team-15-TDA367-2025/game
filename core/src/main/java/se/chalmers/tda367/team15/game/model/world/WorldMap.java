package se.chalmers.tda367.team15.game.model.world;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

public class WorldMap {
    private final int width;
    private final int height;
    private final Tile[][] tiles;

    public WorldMap(int width, int height, TerrainGenerator generator) {
        this.width = width;
        this.height = height;
        this.tiles = generator.generate(width, height);
    }

    public Tile getTile(GridPoint2 pos) {
        if (!isInBounds(pos)) {
            return null;
        }
        return tiles[pos.x][pos.y];
    }

    public boolean isInBounds(GridPoint2 pos) {
        return pos.x >= 0 && pos.x < width && pos.y >= 0 && pos.y < height;
    }


    public GridPoint2 worldToTile(Vector2 worldPos) {
        return new GridPoint2(
            (int) Math.floor(worldPos.x) + width / 2,
            (int) Math.floor(worldPos.y) + height / 2
        );
    }

    public Vector2 tileToWorld(GridPoint2 tilePos) {
        return new Vector2(
            tilePos.x - width / 2f + 0.5f,
            tilePos.y - height / 2f + 0.5f
        );
    }

    public GridPoint2 getSize() {
        return new GridPoint2(width, height);
    }

    /** Returns a reference to the tiles array. DO NOT MODIFY THE RETURNED ARRAY.
     *  We do not copy the array because we'll need to do this each frame.
    */
    public Tile[][] getTiles() {
        return tiles;
    }
}
