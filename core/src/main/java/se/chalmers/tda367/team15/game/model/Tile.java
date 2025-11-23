package se.chalmers.tda367.team15.game.model;

import java.util.List;

public class Tile {
    private int x;
    private int y;
    private boolean isDiscovered;
    private boolean passable;

    public Tile(int x, int y, boolean passable, List<Object> objects, List<Pheromone> pheromones) {
        this.x = x;
        this.y = y;
        this.passable = passable;
        this.isDiscovered = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
