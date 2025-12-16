package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.entity.AttackComponent;
import se.chalmers.tda367.team15.game.model.entity.AttackTarget;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.CanBeAttacked;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.managers.PheromoneManager;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;

public class AttackBehavior extends AntBehavior {
    private final HashMap<AttackCategory, Integer> targetPriority = new HashMap<>();
    private final Home home;
    private final AttackComponent attackComponent;
    private final PheromoneGridConverter converter;

    public AttackBehavior(Home home, Ant ant, Vector2 lastPosBeforeAttack, EntityQuery entityQuery, PheromoneGridConverter converter) {
        super(ant, entityQuery);
        targetPriority.put(AttackCategory.TERMITE, 1);
        this.home = home;
        this.attackComponent = new AttackComponent(5, 1000, 2, ant);
        this.converter = converter;
    }

    @Override
    public void update(PheromoneManager system) {
        AttackTarget target = findTarget();

        if (target == null) {
            ant.setBehavior(new FollowTrailBehavior(home, entityQuery, ant, converter));
        } else {
            Vector2 targetV = target.hasPosition.getPosition().sub(ant.getPosition());
            ant.setVelocity(targetV.nor().scl(ant.getSpeed()));
            attackComponent.attack(target);
        }

    }

    private AttackTarget findTarget() {
        AttackTarget target = null;
        List<CanBeAttacked> entities = entityQuery.getEntitiesOfType(CanBeAttacked.class);
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

    private List<AttackTarget> potentialTargets(List<CanBeAttacked> entities) {
        List<AttackTarget> targets = new ArrayList<>();
        for (CanBeAttacked e : entities) {
            targets.add(new AttackTarget(e, e));
        }
        targets.removeIf(t -> t.canBeAttacked.getFaction() == ant.getFaction());
        targets.removeIf(t -> t.hasPosition.getPosition().dst(ant.getPosition()) > ant.getVisionRadius());

        return targets;
    }
}
