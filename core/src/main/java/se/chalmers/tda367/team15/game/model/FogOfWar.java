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

    public boolean isDiscovered(GridPoint2 pos) {
        if (!worldMap.isInBounds(pos)) {
            return false;
        }
        return discovered[pos.x][pos.y];
    }

    void reveal(GridPoint2 center, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                GridPoint2 pos = new GridPoint2(center.x + dx, center.y + dy);
                boolean insideCircle = dx * dx + dy * dy <= radius * radius;
                if (worldMap.isInBounds(pos) && insideCircle) {
                    discovered[pos.x][pos.y] = true;
                }
            }
        }
    }
}
