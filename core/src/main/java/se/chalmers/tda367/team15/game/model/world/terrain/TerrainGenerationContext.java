package se.chalmers.tda367.team15.game.model.world.terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // Generic maps
    private final Map<String, Object> data;
    
    // Core tile map (final result)
    private Tile[][] tileMap;
    
    // Feature results
    private final List<StructureSpawn> structureSpawns;

    public TerrainGenerationContext(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.random = new Random(seed);
        
        this.data = new HashMap<>();
        this.tileMap = new Tile[width][height];
        this.structureSpawns = new ArrayList<>();
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public long getSeed() { return seed; }
    public Random getRandom() { return random; }

    // Generic Data Access
    public void putData(String key, Object value) {
        data.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        return (T) data.get(key);
    }
    
    public boolean hasData(String key) {
        return data.containsKey(key);
    }

    // Typed Helpers for common maps
    public double[][] getHeightMap() {
        return getData("heightMap");
    }

    public void setHeightMap(double[][] heightMap) {
        putData("heightMap", heightMap);
    }

    public boolean[][] getWaterMap() {
        return getData("waterMap");
    }

    public void setWaterMap(boolean[][] waterMap) {
        putData("waterMap", waterMap);
    }

    public Tile[][] getTileMap() { return tileMap; }
    public void setTileMap(Tile[][] tileMap) { this.tileMap = tileMap; }
    
    public List<StructureSpawn> getStructureSpawns() { return structureSpawns; }
    public void addStructureSpawn(StructureSpawn spawn) { structureSpawns.add(spawn); }

    // Helpers
    public boolean isWater(int x, int y) {
        if (!isInBounds(x, y)) return false;
        boolean[][] waterMap = getWaterMap();
        return waterMap != null && waterMap[x][y];
    }

    public void setTile(int x, int y, Tile tile) {
        if (isInBounds(x, y)) {
            tileMap[x][y] = tile;
        }
    }
    
    public double getHeight(int x, int y) {
        if (!isInBounds(x, y)) return 0.0;
        double[][] heightMap = getHeightMap();
        return heightMap != null ? heightMap[x][y] : 0.0;
    }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
