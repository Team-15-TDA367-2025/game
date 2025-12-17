package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import se.chalmers.tda367.team15.game.model.interfaces.CollisionBehaviour;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;

public interface GeneralizedBehaviour extends CollisionBehaviour {
    void update(PheromoneSystem system);
}
