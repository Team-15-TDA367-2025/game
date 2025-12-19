package se.chalmers.tda367.team15.game.model.entity.enemy;

import java.util.HashMap;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.entity.MeleeAttackBehaviour;
import se.chalmers.tda367.team15.game.model.interfaces.CanAttack;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.StructureProvider;

public class TermiteAttackBehaviour extends MeleeAttackBehaviour {

    public TermiteAttackBehaviour(CanAttack canAttack, EntityQuery entityQuery,
            HashMap<AttackCategory, Integer> targetPriority) {
        super(canAttack, entityQuery,targetPriority);
    }

    @Override
    public void noTargets() {
        host.setVelocity(new Vector2(0, 0));
    }

}
