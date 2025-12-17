package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.termite.*;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.managers.StructureManager;

import java.util.HashMap;

public class EnemyFactory {
    private final DestructionListener destructionListener;
    private final EntityQuery entityQuery;
    private final StructureManager structureManager;
    private final HashMap<AttackCategory, Integer> targetPriority;
    public EnemyFactory(EntityQuery entityQuery, StructureManager structureManager, DestructionListener destructionListener, HashMap<AttackCategory, Integer> targetPriority) {
        this.entityQuery = entityQuery;
        this.structureManager = structureManager;
        this.destructionListener = destructionListener;
        this.targetPriority = targetPriority;
    }

    public Termite createTermite(Vector2 pos) {
        Termite termite =  new Termite(pos, entityQuery, structureManager, destructionListener);
        AttackTargetingComponent attackTargetingComponent = new AttackTargetingComponent(termite,entityQuery,structureManager,targetPriority);
        AttackComponent attackComponent = new AttackComponent(5, 1000, 2.0f, termite);
        TermiteAttackBehaviour attackBehaviour = new TermiteAttackBehaviour(termite,attackTargetingComponent,attackComponent);
        TermiteBehaviourManager termiteBehaviourManager = new TermiteBehaviourManager(attackBehaviour);
        termite.setManager(termiteBehaviourManager);

        return termite;
    }
}
