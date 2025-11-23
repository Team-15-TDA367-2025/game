package se.chalmers.tda367.team15.game.model;

import java.util.List;

public class Map {
    private int size;
    private int discoveredArea;
    private List<Object> objects;
    private Tile[][] tiles;

    public Map(int size, Tile[][] tiles, List<Object> objects) {
        this.size = size;
        this.tiles = tiles;
        this.objects = objects;
        this.discoveredArea = 0;
    }
}
