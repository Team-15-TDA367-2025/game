package se.chalmers.tda367.team15.game.model;

public class FogOfWar {
    private final boolean[][] discovered;
    private final int width;
    private final int height;
    private final float tileSize;

    public FogOfWar(int width, int height, float tileSize) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        discovered = new boolean[width][height];
    }
    
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getTileSize() {
        return tileSize;
    }

    public boolean isDiscovered(int x, int y) {
        return discovered[x][y];
    }

    void reveal(int cx, int cy, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int x = cx + dx;
                int y = cy + dy;
                if (x >= 0 && y >= 0 && x < width && y < height) {
                    discovered[x][y] = true;    
                }
            }
        }
    }
}
