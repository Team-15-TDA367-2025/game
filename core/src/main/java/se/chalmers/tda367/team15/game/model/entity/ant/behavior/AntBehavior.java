package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.CanBeAttacked;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;

public abstract class AntBehavior {
    protected Ant ant;
    protected EntityQuery entityQuery;

    public AntBehavior(Ant ant, EntityQuery entityQuery) {
        this.ant = ant;
        this.entityQuery = entityQuery;
    }
    // TODO code duplication????
    public abstract void update(PheromoneSystem system);

    public boolean enemiesInSight() {
        List<CanBeAttacked> entities = entityQuery.getEntitiesOfType(CanBeAttacked.class);

        entities.removeIf(e -> e.getPosition().dst(ant.getPosition()) > ant.getVisionRadius());

        List<CanBeAttacked> targets = new ArrayList<>();
        for (CanBeAttacked e : entities) {
            targets.add(e);
        }

        targets.removeIf(t -> t.getFaction() == ant.getFaction());

        return !targets.isEmpty();
    }
}
