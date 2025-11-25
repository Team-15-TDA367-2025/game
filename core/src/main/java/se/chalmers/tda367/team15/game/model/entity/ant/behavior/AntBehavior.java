package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import se.chalmers.tda367.team15.game.model.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;

public interface AntBehavior {
    void update(Ant ant, PheromoneSystem system, float deltaTime);
}
