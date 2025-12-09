package se.chalmers.tda367.team15.game.model.world;

import java.util.List;

import com.badlogic.gdx.math.GridPoint2;

/**
 * Result of terrain generation containing both the tile grid and
 * special feature locations like resource nucleation points.
 */
public class TerrainGenerationResult {
    private final Tile[][] tiles;
    private final List<GridPoint2> nucleationPoints;

    public TerrainGenerationResult(Tile[][] tiles, List<GridPoint2> nucleationPoints) {
        this.tiles = tiles;
        this.nucleationPoints = nucleationPoints;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    /**
     * Returns positions where resource nodes should spawn.
     * These locations are guaranteed to be surrounded by grass1 terrain.
     */
    public List<GridPoint2> getNucleationPoints() {
        return nucleationPoints;
    }
}

