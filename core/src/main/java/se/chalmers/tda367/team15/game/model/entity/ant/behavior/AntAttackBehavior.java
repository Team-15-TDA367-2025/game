package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import java.util.HashMap;

import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.entity.MeleeAttackBehaviour;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.managers.StructureManager;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;

public class AntAttackBehavior extends MeleeAttackBehaviour implements GeneralizedBehaviour {
    private final Ant ant;

    public AntAttackBehavior(Ant ant, EntityQuery entityQuery, StructureManager structureManager,HashMap<AttackCategory, Integer> targetPriority) {
        super(ant, entityQuery,structureManager,targetPriority);
        this.ant=ant;

    }

    @Override
    protected void noTargets() {
        ant.setWanderBehaviour();

    }
    @Override
    public void update(PheromoneSystem system) {
        super.update();
    }

    @Override
    public void handleCollision() {
        ant.pickRandomDirection();
    }
}
