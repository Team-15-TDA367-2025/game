package se.chalmers.tda367.team15.game.model.interfaces;

import java.util.List;

/**
 * Generic interface for querying entities by type.
 * Allows consumers to request entities without knowing the concrete EntityManager.
 */
public interface EntityQuery {
    /**
     * Returns all entities that are instances of the given type.
     *
     * Note: {@code type} may be a concrete class (e.g. {@code Ant.class}) or an interface
     * implemented by one or more entities (e.g. {@code VisionProvider.class}).
     */
    <T> List<T> getEntitiesOfType(Class<T> type);
}

