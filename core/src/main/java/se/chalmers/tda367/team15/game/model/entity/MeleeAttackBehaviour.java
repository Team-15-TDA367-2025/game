package se.chalmers.tda367.team15.game.model.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.interfaces.CanAttack;
import se.chalmers.tda367.team15.game.model.interfaces.CanBeAttacked;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;

/**
 * This class is used as the attack logic for all things with melee attack
 * behaviour
 */
public abstract class MeleeAttackBehaviour {
    protected final CanAttack host;

    private final EntityQuery entityQuery;
    private final HashMap<AttackCategory, Integer> targetPriority;
    private long lastAttackTimeMS = 0;

    protected MeleeAttackBehaviour(CanAttack canAttack, EntityQuery entityQuery, HashMap<AttackCategory, Integer> targetPriority) {
        this.host = canAttack;
        this.entityQuery = entityQuery;
        this.targetPriority=targetPriority;
    }

    public void update() {
        CanBeAttacked target = findTarget();
        if (target == null) {
            noTargets();
        } else {
            Vector2 targetV = target.getPosition().sub(this.host.getPosition());
            host.setVelocity(targetV.nor().scl(this.host.getSpeed()));
            attack(target); // Remember, we attack before actually moving
        }
    }

    protected void attack(CanBeAttacked target) {
        long now = System.currentTimeMillis();
        if (now - lastAttackTimeMS > host.getAttackCoolDownMs()) {
            if (target.getPosition().dst(host.getPosition()) <= host.getAttackRange()) {
                lastAttackTimeMS = System.currentTimeMillis();
                target.takeDamage(host.getAttackDamage());
            }
        }
    }

    private CanBeAttacked findTarget() {

        CanBeAttacked target = null;

        List<CanBeAttacked> potentialTargets = potentialTargets();

        // determine target, TargetPriority gives us: ants first, then structures, then
        // stand still.
        if (!potentialTargets.isEmpty()) {
            target = potentialTargets.getFirst();
            for (CanBeAttacked t : potentialTargets) {
                if (isCloserTarget(t, target) && isHigherPriority(t, target)) {
                    target = t;
                }
            }
        }
        return target;
    }

    private boolean isCloserTarget(CanBeAttacked target, CanBeAttacked currentTarget) {
        return target.getPosition().dst(host.getPosition()) < currentTarget.getPosition().dst(host.getPosition());
    }

    private boolean isHigherPriority(CanBeAttacked target, CanBeAttacked currentTarget) {
        return targetPriority.get(target.getAttackCategory()) >= targetPriority.get(currentTarget.getAttackCategory());
    }

    private List<CanBeAttacked> potentialTargets() {
        List<CanBeAttacked> targets = new ArrayList<>(entityQuery.getEntitiesOfType(CanBeAttacked.class));
        targets.removeIf(t -> t.getFaction() == host.getFaction());
        targets.removeIf(t -> t.getPosition().dst(host.getPosition()) > host.getVisionRadius());
        return targets;
    }

    protected abstract void noTargets();
}
