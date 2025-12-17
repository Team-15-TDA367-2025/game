package se.chalmers.tda367.team15.game.model.entity.termite;

import com.badlogic.gdx.math.Vector2;
import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.entity.AttackBehaviour;
import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.interfaces.CanAttack;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.managers.StructureManager;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;

import java.util.HashMap;

public class TermiteAttackBehaviour extends AttackBehaviour {


    public TermiteAttackBehaviour(CanAttack canAttack, EntityQuery entityQuery, StructureManager structureManager, HashMap<AttackCategory, Integer> targetPriority) {
        super(canAttack,entityQuery,structureManager,targetPriority);
    }

    @Override
    public void noTargets() {
        Entity host = this.host.getEntity();
        host.setVelocity(new Vector2(0,0));
    }

}
