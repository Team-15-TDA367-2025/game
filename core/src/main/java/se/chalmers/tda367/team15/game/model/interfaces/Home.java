package se.chalmers.tda367.team15.game.model.interfaces;

import se.chalmers.tda367.team15.game.model.entity.ant.Inventory;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;

/**
 * Represents a home location where ants can deposit resources.
 */
public interface Home extends HasPosition {
    boolean depositResources(Inventory inventory);
    boolean spendResources(ResourceType type, int amount);
    int getTotalResources(ResourceType type);
}

