package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.entity.MeleeAttackBehaviour;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.StructureProvider;
import se.chalmers.tda367.team15.game.model.managers.PheromoneManager;

import java.util.HashMap;

/**
 * The attack behaviour for ants
 */
public class AntAttackBehavior extends MeleeAttackBehaviour implements GeneralizedBehaviour {
    private final Ant ant;

    public AntAttackBehavior(Ant ant, EntityQuery entityQuery, HashMap<AttackCategory, Integer> targetPriority) {
        super(ant, entityQuery, targetPriority);
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
