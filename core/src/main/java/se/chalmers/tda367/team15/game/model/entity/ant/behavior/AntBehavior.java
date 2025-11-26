package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;

public abstract class AntBehavior {
    protected Ant ant;

    public AntBehavior(Ant ant) {
        this.ant = ant;
    }

    public abstract void update(PheromoneSystem system, float deltaTime);
}
