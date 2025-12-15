package se.chalmers.tda367.team15.game.model.world;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

public interface MapProvider {
    /** Converts a world position to the closest tile */
    GridPoint2 worldToTile(Vector2 worldPos);
    /** Converts tile coordinates to the center of the tile in world coordinates */
    Vector2 tileToWorld(GridPoint2 tilePos);
    /** Checks if a position is within the map bounds */
    boolean isInBounds(Vector2 worldPos);
    /** Checks if a position is within the map bounds */
    boolean isInBounds(GridPoint2 tilePos);
    /** Gets the size of the map */
    GridPoint2 getSize();
}
