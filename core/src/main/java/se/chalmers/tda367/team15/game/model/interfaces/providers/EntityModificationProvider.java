package se.chalmers.tda367.team15.game.model.interfaces.providers;

import se.chalmers.tda367.team15.game.model.entity.Entity;

public interface EntityModificationProvider {
    void addEntity(Entity entity);

    void removeEntity(Entity entity);
}
