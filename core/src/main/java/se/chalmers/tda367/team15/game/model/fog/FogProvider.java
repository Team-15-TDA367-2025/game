package se.chalmers.tda367.team15.game.model.fog;

import com.badlogic.gdx.math.GridPoint2;

public interface FogProvider {
    GridPoint2 getSize();
    boolean isDiscovered(GridPoint2 pos);
    
    /**
     * Returns the raw discovered array for efficient rendering.
     * DO NOT MODIFY the returned array.
     */
    boolean[][] getDiscoveredArray();
    
    /**
     * Returns true if the fog state has changed since last clearDirty() call.
     */
    boolean isDirty();
    
    /**
     * Clears the dirty flag after the renderer has updated.
     */
    void clearDirty();
}
