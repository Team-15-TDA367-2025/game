package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import se.chalmers.tda367.team15.game.model.entity.MeleeAttackBehaviour;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.StructureProvider;
import se.chalmers.tda367.team15.game.model.managers.PheromoneManager;

/**
 * The attack behaviour for ants
 */
public class AntAttackBehavior extends MeleeAttackBehaviour implements GeneralizedBehaviour {
    private final Ant ant;

    public AntAttackBehavior(Ant ant, EntityQuery entityQuery, StructureProvider structureProvider) {
        super(ant, entityQuery);
        this.ant = ant;
    }

    @Override
    protected void noTargets() {
        ant.setWanderBehaviour();
    }

    @Override
    public void update(PheromoneManager system) {
        super.update();
    }

    @Override
    public void handleCollision() {
        ant.pickRandomDirection();
    }
}
