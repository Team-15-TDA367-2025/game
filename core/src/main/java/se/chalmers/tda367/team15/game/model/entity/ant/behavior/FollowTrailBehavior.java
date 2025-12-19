package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.trail.TrailStrategy;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.managers.PheromoneManager;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;

/**
 * This behaviour is used when ants are trying to follow a pheromone trail
 */
public class FollowTrailBehavior extends AntBehavior {
    private static final float REACHED_THRESHOLD_FRACTION = 0.3f;
    private Pheromone lastPheromone = null;
    private Pheromone currentTarget = null;
    private final float reachedThresholdSq;
    private final PheromoneGridConverter converter;
    private final TrailStrategy trailStrategy;

    public FollowTrailBehavior(EntityQuery entityQuery, Ant ant,
            PheromoneGridConverter converter, TrailStrategy trailStrategy) {
        super(ant, entityQuery);
        float cellSize = converter.getPheromoneCellSize();
        float threshold = cellSize * REACHED_THRESHOLD_FRACTION;
        this.reachedThresholdSq = threshold * threshold;
        this.converter = converter;
        this.trailStrategy = trailStrategy;
    }

    @Override
    public void update(PheromoneManager system) {
        if (enemiesInSight()) {
            exitTrail(); // Decrement soldier count when leaving trail
            ant.setAttackBehaviour();
            return;
        }

        List<Pheromone> neighbors = system.getPheromonesIn3x3(ant.getGridPosition()).stream()
                .filter(p -> ant.getType().allowedPheromones().contains(p.getType()))
                .collect(Collectors.toList());

        // Safety check: if no pheromones nearby, trail was likely deleted
        if (neighbors.isEmpty()) {
            exitTrail(); // Decrement soldier count when leaving trail
            lastPheromone = null;
            currentTarget = null;
            trailStrategy.onTrailEnd(ant, null);
            return;
        }

        // 1. Initialization / Re-anchoring
        if (lastPheromone == null || !isPheromoneStillValid(lastPheromone, neighbors)) {
            Pheromone newPheromone = neighbors.stream()
                    .min((a, b) -> Integer.compare(a.getDistance(), b.getDistance()))
                    .orElse(null);

            if (newPheromone == null) {
                exitTrail();
                trailStrategy.onTrailEnd(ant, null);
                return;
            }

            updateSoldierCount(lastPheromone, newPheromone);
            lastPheromone = newPheromone;
            // Reset target since our anchor changed
            currentTarget = null;
        }

        // 2. Target Management - check if current target is still valid
        if (currentTarget != null) {
            if (!isPheromoneStillValid(currentTarget, neighbors)) {
                // Target was deleted, need new target
                currentTarget = null;
            } else if (ant.getPosition().dst2(getCenterPos(currentTarget)) < reachedThresholdSq) {
                // Reached target - update soldier count when moving to new cell
                updateSoldierCount(lastPheromone, currentTarget);
                lastPheromone = currentTarget;
                currentTarget = null;
            }
        }

        // 3. Select next target using strategy
        if (currentTarget == null) {
            currentTarget = trailStrategy.selectNextPheromone(ant, neighbors, lastPheromone);

            if (currentTarget == null) {
                trailStrategy.onTrailEnd(ant, lastPheromone);
                return;
            }
        }

        // 4. Movement toward target
        Vector2 targetPos = getCenterPos(currentTarget);
        Vector2 diff = new Vector2(targetPos).sub(ant.getPosition());
        float distSq = diff.len2();

        if (distSq > 0.001f) {
            float maxSpeed = ant.getSpeed() * trailStrategy.getSpeedMultiplier();
            float cellSize = converter.getPheromoneCellSize();
            float speed = Math.min(maxSpeed, Math.max(ant.getSpeed(), (float) Math.sqrt(distSq) / cellSize * maxSpeed));
            ant.setVelocity(diff.nor().scl(speed));
        } else {
            ant.setVelocity(new Vector2(0, 0));
        }
    }

    /**
     * Check if a pheromone is still in the neighbors list (not deleted).
     */
    private boolean isPheromoneStillValid(Pheromone pheromone, List<Pheromone> neighbors) {
        return neighbors.stream()
                .anyMatch(p -> p.getPosition().equals(pheromone.getPosition())
                        && p.getType() == pheromone.getType());
    }

    private Vector2 getCenterPos(Pheromone p) {
        return converter.pheromoneGridToWorld(p.getPosition());
    }

    public Pheromone getCurrentPheromone() {
        return currentTarget;
    }

    @Override
    public void handleCollision() {
        currentTarget = null;
    }

    /**
     * Checks if this ant is a soldier (can follow ATTACK pheromones).
     */
    private boolean isSoldierAnt() {
        return ant.getType().allowedPheromones().contains(PheromoneType.ATTACK);
    }

    /**
     * Updates soldier count when moving from one pheromone cell to another.
     * Only affects soldier ants.
     */
    private void updateSoldierCount(Pheromone oldPheromone, Pheromone newPheromone) {
        if (!isSoldierAnt()) {
            return;
        }
        if (oldPheromone != null && oldPheromone != newPheromone) {
            oldPheromone.decrementSoldierCount();
        }
        if (newPheromone != null && newPheromone != oldPheromone) {
            newPheromone.incrementSoldierCount();
        }
    }

    /**
     * Called when ant leaves the trail (switches behavior or trail ends).
     * Decrements soldier count on current pheromone.
     */
    private void exitTrail() {
        if (isSoldierAnt() && lastPheromone != null) {
            lastPheromone.decrementSoldierCount();
        }
    }
}
