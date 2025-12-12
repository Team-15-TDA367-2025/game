package se.chalmers.tda367.team15.game.model.interfaces;

import java.util.List;

import se.chalmers.tda367.team15.game.model.entity.Entity;

/**
 * Generic interface for querying entities by type.
 * Allows consumers to request entities without knowing the concrete EntityManager.
 */
public interface EntityQuery {
    <T extends Entity> List<T> getEntitiesOfType(Class<T> type);
}

