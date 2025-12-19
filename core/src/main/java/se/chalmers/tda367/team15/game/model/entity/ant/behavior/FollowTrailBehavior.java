package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import java.util.List;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.providers.PheromoneUsageProvider;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;

/**
 * Behavior for ants following pheromone trails.
 * Each frame: move toward target pheromone, pick new target when reached.
 */
public class FollowTrailBehavior extends AntBehavior {
    private final PheromoneGridConverter converter;
    private Pheromone currentPheromone = null;
    private Pheromone targetPheromone = null; // The pheromone we're moving towards
    private boolean outwards = true; // Per-ant direction state

    public FollowTrailBehavior(EntityQuery entityQuery, Ant ant, PheromoneGridConverter converter) {
        super(ant, entityQuery);
        this.converter = converter;
    }

    @Override
    public void update(PheromoneUsageProvider system) {
        if (enemiesInSight()) {
            exitTrail();
            ant.setAttackBehaviour();
            return;
        }

        // If we have a target, check if we've reached it
        if (targetPheromone != null) {
            Vector2 targetPos = converter.pheromoneGridToWorld(targetPheromone.getPosition());
            float distToTarget = ant.getPosition().dst2(targetPos);

            // If we haven't reached the target yet, keep moving towards it
            if (distToTarget > 0.01f) { // Small threshold
                Vector2 diff = new Vector2(targetPos).sub(ant.getPosition());
                float speed = ant.getSpeed() * ant.getType().trailStrategy().getSpeedMultiplier();
                ant.setVelocity(diff.nor().scl(speed));
                return;
            }

            // We've reached the target - update current pheromone
            updateAntCount(targetPheromone);
            currentPheromone = targetPheromone;
            targetPheromone = null;
        }

        // Need to pick a new target
        Pheromone current = currentPheromone;

        // If we don't have a current pheromone, find one at ant's position
        if (current == null) {
            GridPoint2 gridPos = ant.getGridPosition();
            current = findCurrentPheromone(system, gridPos);
            if (current == null) {
                // Try to find any nearby pheromone
                List<Pheromone> nearby = system.getPheromonesIn3x3(gridPos, ant.getType().allowedPheromones())
                        .stream().toList();
                if (!nearby.isEmpty()) {
                    current = nearby.stream()
                            .min((a, b) -> Integer.compare(a.getDistance(), b.getDistance()))
                            .orElse(null);
                }
            }
            if (current == null) {
                ant.getType().trailStrategy().onTrailEnd(ant, null, this);
                return;
            }
            updateAntCount(current);
            currentPheromone = current;
        }

        // Get neighbors around the current pheromone's position
        List<Pheromone> neighbors = system.getPheromonesIn3x3(current.getPosition(), ant.getType().allowedPheromones())
                .stream().toList();

        if (neighbors.isEmpty()) {
            exitTrail();
            ant.getType().trailStrategy().onTrailEnd(ant, null, this);
            return;
        }

        // Select next target using strategy
        Pheromone next = ant.getType().trailStrategy().selectNextPheromone(ant, neighbors, current, this);

        if (next == null) {
            ant.getType().trailStrategy().onTrailEnd(ant, current, this);
            return;
        }

        // Set as new target
        targetPheromone = next;

        // Start moving toward target
        Vector2 targetPos = converter.pheromoneGridToWorld(next.getPosition());
        Vector2 diff = new Vector2(targetPos).sub(ant.getPosition());

        if (diff.len2() > 0.001f) {
            float speed = ant.getSpeed() * ant.getType().trailStrategy().getSpeedMultiplier();
            ant.setVelocity(diff.nor().scl(speed));
        }
    }

    /**
     * Finds the pheromone at the ant's current grid position.
     */
    private Pheromone findCurrentPheromone(PheromoneUsageProvider system, GridPoint2 gridPos) {
        for (PheromoneType type : ant.getType().allowedPheromones()) {
            Pheromone p = system.getPheromoneAt(gridPos, type);
            if (p != null) {
                return p;
            }
        }
        return null;
    }

    /**
     * Updates ant count when moving between pheromone cells.
     */
    private void updateAntCount(Pheromone current) {
        if (current != currentPheromone) {
            if (currentPheromone != null) {
                currentPheromone.decrementAnts();
            }
            if (current != null) {
                current.incrementAnts();
            }
            currentPheromone = current;
        }
    }

    /**
     * Called when ant leaves the trail.
     */
    private void exitTrail() {
        if (currentPheromone != null) {
            currentPheromone.decrementAnts();
            currentPheromone = null;
        }
        targetPheromone = null;
    }

    public Pheromone getCurrentPheromone() {
        return currentPheromone;
    }

    public boolean isOutwards() {
        return outwards;
    }

    public void setOutwards(boolean outwards) {
        this.outwards = outwards;
    }

    public void flipDirection() {
        this.outwards = !this.outwards;
    }

    @Override
    public void handleCollision() {
        // Clear target to re-evaluate path
        targetPheromone = null;
    }
}
