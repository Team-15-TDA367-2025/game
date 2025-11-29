package se.chalmers.tda367.team15.game.model.entity;

import com.badlogic.gdx.math.Vector2;

import java.util.Vector;

/**
 * Represents the notion that the Object can be destroyed or die in the game world.
 */
public interface HasHealth {
    /**
     * Instructs the object to take damage.
     * @param amount the damage taken.
     */
    void takeDamage(float amount);

    /**
     * Instructs the object to notify the {@link se.chalmers.tda367.team15.game.model.DestructionListener} to remove it from the {@link se.chalmers.tda367.team15.game.model.GameWorld}
     */
    void die();
    //Code duplication, but when we have instance of either structure or entity it makes code a lot cleaner.
    Vector2 getPosition();
}
