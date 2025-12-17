package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.entity.AttackBehaviour;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.CanBeAttacked;
import se.chalmers.tda367.team15.game.model.interfaces.CollisionBehaviour;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.managers.StructureManager;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;

public class AntAttackBehavior extends AttackBehaviour implements GeneralizedBehaviour {
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
