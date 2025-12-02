package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.GridPoint2;
import se.chalmers.tda367.team15.game.model.world.WorldMap;

public class FogOfWar {
    private final boolean[][] discovered;
    private final WorldMap worldMap;

    public FogOfWar(WorldMap worldMap) {
        this.worldMap = worldMap;
        GridPoint2 size = worldMap.getSize();
        discovered = new boolean[size.x][size.y];
    }

    public GridPoint2 getSize() {
        return worldMap.getSize();
    }

    public float getTileSize() {
        return worldMap.getTileSize();
    }

    public boolean isDiscovered(int x, int y) {
        if (!worldMap.isInBounds(x, y)) {
            return false;
        }
        return discovered[x][y];
    }

    public boolean isDiscovered(GridPoint2 tilePos) {
        return isDiscovered(tilePos.x, tilePos.y);
    }

    void reveal(GridPoint2 center, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int x = center.x + dx;
                int y = center.y + dy;
                boolean insideCircle = dx * dx + dy * dy <= radius * radius;
                if (worldMap.isInBounds(x, y) && insideCircle) {
                    discovered[x][y] = true;    
                }
            }
        }
    }
}
