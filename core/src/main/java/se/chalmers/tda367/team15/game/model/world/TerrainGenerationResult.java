package se.chalmers.tda367.team15.game.model.world;

import java.util.List;

import se.chalmers.tda367.team15.game.model.world.terrain.StructureSpawn;

/**
 * Result of terrain generation containing both the tile grid and
 * special feature locations like structure spawns.
 */
public class TerrainGenerationResult {
    private final Tile[][] tiles;
    private final List<StructureSpawn> structureSpawns;

    public TerrainGenerationResult(Tile[][] tiles, List<StructureSpawn> structureSpawns) {
        this.tiles = tiles;
        this.structureSpawns = structureSpawns;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public List<StructureSpawn> getStructureSpawns() {
        return structureSpawns;
    }
}
