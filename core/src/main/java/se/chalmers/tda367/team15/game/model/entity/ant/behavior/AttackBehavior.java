package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.GameWorld;
import se.chalmers.tda367.team15.game.model.entity.AttackComponent;
import se.chalmers.tda367.team15.game.model.entity.AttackTarget;
import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.CanBeAttacked;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;

public class AttackBehavior extends AntBehavior {
    private GameWorld gameWorld;
    HashMap<AttackCategory, Integer> targetPriority = new HashMap<>();
    Vector2 lastPosBeforeAttack;
    AttackComponent attackComponent;

    public AttackBehavior(Ant ant, Vector2 lastPosBeforeAttack, GameWorld gameWorld) {
        super(ant);
        targetPriority.put(AttackCategory.TERMITE, 1);
        this.gameWorld = gameWorld;
        this.lastPosBeforeAttack = lastPosBeforeAttack;
        this.attackComponent = new AttackComponent(5, 1000, 2, ant);
    }

    @Override
    public void update(PheromoneSystem system) {
        AttackTarget target = findTarget();

        if (target == null) {
            ant.setBehavior(new FollowTrailBehavior(ant, gameWorld));
        } else {
            Vector2 targetV = target.hasPosition.getPosition().sub(ant.getPosition());
            ant.setVelocity(targetV.nor().scl(ant.getSpeed()));
            attackComponent.attack(target);
        }

    }

    private AttackTarget findTarget() {
        AttackTarget target = null;
        List<Entity> entities = ant.getGameWorld().getEntities();
        List<AttackTarget> potentialTargets = potentialTargets(entities);

        // determine target
        if (!potentialTargets.isEmpty()) {
            target = potentialTargets.getFirst();
            for (AttackTarget t : potentialTargets) {
                // Greater or equal target priority?
                if (targetPriority.get(t.canBeAttacked.getAttackCategory()) >= targetPriority
                        .get(target.canBeAttacked.getAttackCategory())) {
                    // closest distance?
                    if (t.hasPosition.getPosition().dst(ant.getPosition()) < target.hasPosition.getPosition()
                            .dst(ant.getPosition())) {
                        target = t;
                    }
                }
            }
        }

        return target;
    }

    private List<AttackTarget> potentialTargets(List<Entity> entities) {
        List<AttackTarget> targets = new ArrayList<>();
        for (Entity e : entities) {
            if (e instanceof CanBeAttacked) {
                targets.add(new AttackTarget((CanBeAttacked) e, e));
            }
        }
        targets.removeIf(t -> t.canBeAttacked.getFaction() == ant.getFaction());
        targets.removeIf(t -> t.hasPosition.getPosition().dst(ant.getPosition()) > ant.getVisionRadius());

        return targets;
    }
}
