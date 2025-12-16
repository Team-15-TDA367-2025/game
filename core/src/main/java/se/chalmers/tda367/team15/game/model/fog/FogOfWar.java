package se.chalmers.tda367.team15.game.model.fog;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.world.MapProvider;

// Data store for fog of war, only used by FogManager
class FogOfWar implements FogProvider {
    private final boolean[][] discovered;
    private final MapProvider mapProvider;

    public FogOfWar(MapProvider mapProvider) {
        this.mapProvider = mapProvider;
        GridPoint2 size = mapProvider.getSize();
        discovered = new boolean[size.x][size.y];
    }

    public GridPoint2 getSize() {
        return mapProvider.getSize();
    }

    public boolean isDiscovered(GridPoint2 pos) {
        if (!mapProvider.isInBounds(pos)) {
            return false;
        }
        return discovered[pos.x][pos.y];
    }

    void reveal(GridPoint2 center, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                GridPoint2 pos = new GridPoint2(center.x + dx, center.y + dy);
                boolean insideCircle = dx * dx + dy * dy <= radius * radius;
                if (mapProvider.isInBounds(pos) && insideCircle) {
                    discovered[pos.x][pos.y] = true;
                }
            }
        }
    }
}
