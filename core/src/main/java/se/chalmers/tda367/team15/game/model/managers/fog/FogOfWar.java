package se.chalmers.tda367.team15.game.model.managers.fog;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.world.MapProvider;

// Data store for fog of war, only used by FogManager
public class FogOfWar {
    private final boolean[][] discovered;
    private final MapProvider mapProvider;
    private boolean dirty = true;

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

    public boolean[][] getDiscoveredArray() {
        return discovered;
    }

    public void reveal(GridPoint2 center, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                GridPoint2 pos = new GridPoint2(center.x + dx, center.y + dy);
                boolean insideCircle = dx * dx + dy * dy <= radius * radius;

                if (mapProvider.isInBounds(pos) && insideCircle) {
                    if (!discovered[pos.x][pos.y]) {
                        discovered[pos.x][pos.y] = true;
                        dirty = true;
                    }
                }
            }
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearDirty() {
        dirty = false;
    }
}
