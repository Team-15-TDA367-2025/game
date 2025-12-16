package se.chalmers.tda367.team15.game.model.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.interfaces.EntityDeathObserver;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.Updatable;

/**
 * Manages the lifecycle of entities in the simulation.
 * Owns all entities, handles updates, and cleans up on death.
 */
public class EntityManager implements Updatable, EntityDeathObserver, EntityQuery {
    private final List<Entity> entities = new ArrayList<>();

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    @Override
    public <T> List<T> getEntitiesOfType(Class<T> type) {
        List<T> result = new ArrayList<>();
        for (Entity entity : entities) {
            if (type.isInstance(entity)) {
                result.add(type.cast(entity));
            }
        }
        return result;
    }

    @Override
    public void update(float deltaTime) {
        List<Entity> toUpdate = new ArrayList<>(entities);
        for (Entity entity : toUpdate) {
            entity.update(deltaTime);
        }
    }

    @Override
    public void onEntityDeath(Entity e) {
        entities.remove(e);
    }
}
