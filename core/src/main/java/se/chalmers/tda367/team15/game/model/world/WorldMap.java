package se.chalmers.tda367.team15.game.model.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.world.terrain.StructureSpawn;

public class WorldMap {
    private final int width;
    private final int height;
    private final Tile[][] tiles;
    private final Set<String> textureNames;
    private final List<StructureSpawn> structureSpawns;

    public WorldMap(int width, int height, TerrainGenerator generator) {
        this.width = width;
        this.height = height;
        TerrainGenerationResult result = generator.generate(width, height);
        this.tiles = result.getTiles();
        this.structureSpawns = new ArrayList<>(result.getStructureSpawns());
        this.textureNames = collectTextureNames();
    }

    private Set<String> collectTextureNames() {
        Set<String> names = new HashSet<>();
        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                names.add(tile.getTextureName());
            }
        }
        return names;
    }

    public Set<String> getTextureNames() {
        return textureNames;
    }

    public Tile getTile(Vector2 worldPos) {
        GridPoint2 tilePos = worldToTile(worldPos);
        return getTile(tilePos);
    }

    public Tile getTile(GridPoint2 pos) {
        if (!isInBounds(pos)) {
            return null;
        }
        return tiles[pos.x][pos.y];
    }

    public boolean isInBounds(Vector2 worldPos) {
        GridPoint2 tilePos = worldToTile(worldPos);
        return isInBounds(tilePos);
    }

    public boolean isInBounds(GridPoint2 pos) {
        return pos.x >= 0 && pos.x < width && pos.y >= 0 && pos.y < height;
    }

    public GridPoint2 worldToTile(Vector2 worldPos) {
        return new GridPoint2(
                (int) Math.floor(worldPos.x) + width / 2,
                (int) Math.floor(worldPos.y) + height / 2);
    }

    public Vector2 tileToWorld(GridPoint2 tilePos) {
        return new Vector2(
                tilePos.x - width / 2f + 0.5f,
                tilePos.y - height / 2f + 0.5f);
    }

    public GridPoint2 getSize() {
        return new GridPoint2(width, height);
    }

    /**
     * Returns a reference to the tiles array. DO NOT MODIFY THE RETURNED ARRAY.
     * We do not copy the array because we'll need to do this each frame.
     */
    public Tile[][] getTiles() {
        return tiles;
    }

    /**
     * Returns the structure spawns generated during terrain creation.
     */
    public List<StructureSpawn> getStructureSpawns() {
        return Collections.unmodifiableList(structureSpawns);
    }
}
