package se.chalmers.tda367.team15.game.model.interfaces;

import se.chalmers.tda367.team15.game.model.entity.ant.Inventory;

/**
 * Represents a home location where ants can deposit resources.
 */
public interface Home extends HasPosition {
    boolean depositResources(Inventory inventory);
}

