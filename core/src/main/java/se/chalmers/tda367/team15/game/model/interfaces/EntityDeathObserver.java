package se.chalmers.tda367.team15.game.model.interfaces;

import se.chalmers.tda367.team15.game.model.entity.Entity;

/**
 * Classes that implement this interface are interested in when and which entity has died.
 */
public interface EntityDeathObserver {
    /**
     * what to do on entity death
     * @param e the {@link Entity} that died.
     */
    void onEntityDeath(Entity e);
}
