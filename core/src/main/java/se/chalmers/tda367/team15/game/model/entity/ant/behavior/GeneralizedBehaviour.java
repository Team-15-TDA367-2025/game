package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.BehaviourWithCollision;
import se.chalmers.tda367.team15.game.model.managers.PheromoneManager;

/**
 * Used as an abstraction layer that allows {@link Ant} to use generalized
 * attack logic of
 * {@link se.chalmers.tda367.team15.game.model.entity.MeleeAttackBehaviour}
 * While also using its own specific pheromone related behaviour
 */
public interface GeneralizedBehaviour extends BehaviourWithCollision {
    void update(PheromoneManager system);
}
