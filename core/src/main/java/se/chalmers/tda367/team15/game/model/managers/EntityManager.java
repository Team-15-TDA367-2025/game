package se.chalmers.tda367.team15.game.model.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.interfaces.EntityDeathObserver;
import se.chalmers.tda367.team15.game.model.interfaces.EntityModificationProvider;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.SimulationObserver;

/**
 * Manages the lifecycle of entities in the simulation.
 * Owns all entities, handles updates, and cleans up on death.
 *
 * Has a cache of entities by type to avoid lagging when querying entities by
 * type.
 */
public class EntityManager implements SimulationObserver, EntityDeathObserver, EntityQuery, EntityModificationProvider {
    private final List<Entity> entities = new CopyOnWriteArrayList<>();
    private final Map<Class<?>, List<Entity>> cachedEntities = new HashMap<>();

    public void addEntity(Entity entity) {
        entities.add(entity);
        for (Class<?> type : cachedEntities.keySet()) {
            if (type.isInstance(entity)) {
                cachedEntities.get(type).add(entity);
            }
        }
    }

    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    private void cacheEntities(Class<?> type) {
        cachedEntities.putIfAbsent(type, new ArrayList<>());
        for (Entity entity : entities) {
            if (type.isInstance(entity)) {
                cachedEntities.get(type).add(entity);
            }
        }
    }

    @Override
    public <T> List<T> getEntitiesOfType(Class<T> type) {
        if (!cachedEntities.containsKey(type)) {
            cacheEntities(type);
        }
        @SuppressWarnings("unchecked") // We know the type is correct
        List<T> result = (List<T>) cachedEntities.get(type);

        return Collections.unmodifiableList(result);
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            entity.update(deltaTime);
        }
    }

    @Override
    public void removeEntity(Entity entity) {
        entities.remove(entity);
        for (Class<?> type : cachedEntities.keySet()) {
            if (type.isInstance(entity)) {
                cachedEntities.get(type).remove(entity);
            }
        }
    }

    @Override
    public void onEntityDeath(Entity entity) {
        removeEntity(entity);
    }
}
