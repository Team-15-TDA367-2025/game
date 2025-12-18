package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import se.chalmers.tda367.team15.game.model.interfaces.CollisionBehaviour;
import se.chalmers.tda367.team15.game.model.managers.PheromoneManager;

public interface GeneralizedBehaviour extends CollisionBehaviour {
    void update(PheromoneManager system);
}
