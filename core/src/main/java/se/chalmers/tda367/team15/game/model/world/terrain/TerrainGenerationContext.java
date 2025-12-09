package se.chalmers.tda367.team15.game.model.world.terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import se.chalmers.tda367.team15.game.model.world.Tile;

/**
 * Context object passed through the terrain generation pipeline.
 * Holds all mutable state for the current generation process.
 */
public class TerrainGenerationContext {
    private final int width;
    private final int height;
    private final long seed;
    private final Random random;

    // Generation maps
    private double[][] heightMap;
    private boolean[][] waterMap;
    private Tile[][] tileMap;

    // Feature results
    private final List<StructureSpawn> structureSpawns;

    public TerrainGenerationContext(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.random = new Random(seed);
        
        this.heightMap = new double[width][height];
        this.waterMap = new boolean[width][height];
        this.tileMap = new Tile[width][height];
        this.structureSpawns = new ArrayList<>();
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public long getSeed() { return seed; }
    public Random getRandom() { return random; }

    // Map Accessors
    public double[][] getHeightMap() { return heightMap; }
    public void setHeightMap(double[][] heightMap) { this.heightMap = heightMap; }

    public boolean[][] getWaterMap() { return waterMap; }
    public void setWaterMap(boolean[][] waterMap) { this.waterMap = waterMap; }

    public Tile[][] getTileMap() { return tileMap; }
    
    // Structure Spawns
    public List<StructureSpawn> getStructureSpawns() { return structureSpawns; }
    public void addStructureSpawn(StructureSpawn spawn) { structureSpawns.add(spawn); }

    // Helpers
    public boolean isWater(int x, int y) {
        if (!isInBounds(x, y)) return false;
        return waterMap[x][y];
    }

    public void setTile(int x, int y, Tile tile) {
        if (isInBounds(x, y)) {
            tileMap[x][y] = tile;
        }
    }
    
    public double getHeight(int x, int y) {
        if (!isInBounds(x, y)) return 0.0;
        return heightMap[x][y];
    }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}

