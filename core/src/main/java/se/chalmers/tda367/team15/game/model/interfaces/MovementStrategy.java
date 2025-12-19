package se.chalmers.tda367.team15.game.model.interfaces;

import com.badlogic.gdx.math.Vector2;

public interface MovementStrategy {
    /**
     * Checks if the proposed position is valid.
     * 
     * @param position The world position to check.
     * @return true if the entity can move there.
     */
    boolean canMoveTo(Vector2 position);
}
