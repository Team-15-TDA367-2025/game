package se.chalmers.tda367.team15.game.model.interfaces;

/**
 * Represents the notion for an update algorithm of an entity to interact with
 * terrain that is potentially impassable.
 */
public interface BehaviourWithCollision {
    void handleCollision();
}
