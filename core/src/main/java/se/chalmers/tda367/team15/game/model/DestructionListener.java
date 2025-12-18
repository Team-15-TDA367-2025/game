package se.chalmers.tda367.team15.game.model;

import java.util.LinkedHashSet;

import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.interfaces.EntityDeathObserver;
import se.chalmers.tda367.team15.game.model.structure.Structure;

/**
 * Has the responsibility of notifying interested parties when something dies /
 * is destroyed.
 */
public class DestructionListener {
    private final LinkedHashSet<EntityDeathObserver> entityObservers;

    public DestructionListener() {
        this.entityObservers = new LinkedHashSet<>();
    }

    public void addEntityDeathObserver(EntityDeathObserver obs) {
        entityObservers.add(obs);
    }

    public void removeEntityDeathObserver(EntityDeathObserver obs) {
        entityObservers.remove(obs);
    }

    public void notifyEntityDeathObservers(Entity e) {
        for (EntityDeathObserver edo : entityObservers) {
            edo.onEntityDeath(e);
        }
    }

}
