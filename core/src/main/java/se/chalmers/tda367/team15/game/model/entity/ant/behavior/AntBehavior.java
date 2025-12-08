package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.CanBeAttacked;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;

public abstract class AntBehavior {
    protected Ant ant;

    public AntBehavior(Ant ant) {
        this.ant = ant;
    }

    public boolean enemiesInSight() {
        List<Entity> entities = new ArrayList<>(ant.getGameWorld().getEntities());

        entities.removeIf(e -> e.getPosition().dst(ant.getPosition()) > ant.getVisionRadius());

        List<CanBeAttacked> targets = new ArrayList<>();
        for (Entity e : entities) {
            if (e instanceof CanBeAttacked) {
                targets.add((CanBeAttacked) e);
            }
        }

        targets.removeIf(t -> t.getFaction() == ant.getFaction());

        return !targets.isEmpty();
    }

    public abstract void update(PheromoneSystem system, float deltaTime);

}
