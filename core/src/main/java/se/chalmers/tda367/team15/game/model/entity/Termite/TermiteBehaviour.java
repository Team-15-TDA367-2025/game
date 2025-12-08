package se.chalmers.tda367.team15.game.model.entity.Termite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.entity.AttackTarget;
import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.interfaces.CanBeAttacked;
import se.chalmers.tda367.team15.game.model.structure.Structure;

/**
 * Component that handles the behaviour of {@link Termite}
 */
public class TermiteBehaviour {
    Termite termite;

    HashMap<AttackCategory, Integer> targetPriority = new HashMap<>();

    TermiteBehaviour(Termite termite) {
        this.termite = termite;
        targetPriority.put(AttackCategory.WORKER_ANT, 2);
        targetPriority.put(AttackCategory.ANT_COLONY, 1);
    }

    /**
     * Updates the behaviour of the Termite.
     *
     * @param entities   list of entities in the
     *                   {@link se.chalmers.tda367.team15.game.model.GameWorld}
     * @param structures the list of structures in the
     *                   {@link se.chalmers.tda367.team15.game.model.GameWorld}
     * @return the target {@link CanBeAttacked} or {@code null} if there are no
     *         attack targets.
     */

    public AttackTarget update(List<Entity> entities, List<Structure> structures) {

        AttackTarget target = null;

        List<AttackTarget> potentialTargets = potentialTargets(entities, structures);

        // determine target, entities first, then structures, then stand still.
        if (!potentialTargets.isEmpty()) {
            target = potentialTargets.getFirst();
            for (AttackTarget t : potentialTargets) {
                // Greater or equal target priority?
                if (targetPriority.get(t.canBeAttacked.getAttackCategory()) >= targetPriority
                        .get(target.canBeAttacked.getAttackCategory())) {
                    // closest distance?
                    if (t.hasPosition.getPosition().dst(termite.getPosition()) < target.hasPosition.getPosition()
                            .dst(termite.getPosition())) {
                        target = t;
                    }
                }
            }

        }

        if (target == null) {
            termite.setVelocity(new Vector2(0, 0));
        } else {
            Vector2 targetV = target.hasPosition.getPosition().sub(termite.getPosition());
            termite.setVelocity(targetV.nor().scl(termite.getSpeed()));
        }
        return target;
    }

    List<AttackTarget> potentialTargets(List<Entity> entities, List<Structure> structures) {
        List<AttackTarget> targets = new ArrayList<>();
        for (Entity e : entities) {
            if (e instanceof CanBeAttacked) {
                targets.add(new AttackTarget((CanBeAttacked) e, e));
            }
        }

        for (Structure s : structures) {
            if (s instanceof CanBeAttacked) {
                targets.add(new AttackTarget((CanBeAttacked) s, s));
            }
        }

        targets.removeIf(t -> t.canBeAttacked.getFaction() == termite.getFaction());

        return targets;
    }
}
