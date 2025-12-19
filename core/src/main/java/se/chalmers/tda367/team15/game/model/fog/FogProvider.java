package se.chalmers.tda367.team15.game.model.fog;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.interfaces.FogObserver;

public interface FogProvider {
    GridPoint2 getSize();
    boolean isDiscovered(GridPoint2 pos);
    
    /**
     * Returns the raw discovered array for efficient rendering.
     * DO NOT MODIFY the returned array.
     */
    boolean[][] getDiscoveredArray();

    /**
     * Adds an observer to the fog provider.
     * The observer will be notified when the fog state changes.
     */
    void addObserver(FogObserver observer);

    /**
     * Removes an observer from the fog provider.
     */
    void removeObserver(FogObserver observer);
}
