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

/**
 * This behaviour is used when ants are trying to follow a pheromone trail
 */
public class FollowTrailBehavior extends AntBehavior implements GeneralizedBehaviour {
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
            ant.setAttackBehaviour();
            return;
        }

        List<Pheromone> neighbors = system.getPheromonesIn3x3(ant.getGridPosition()).stream()
                .filter(p -> ant.getType().allowedPheromones().contains(p.getType()))
                .collect(Collectors.toList());

        // 1. Initialization / Re-anchoring
        if (lastPheromone == null) {
            lastPheromone = neighbors.stream()
                    .min((a, b) -> Integer.compare(a.getDistance(), b.getDistance()))
                    .orElse(null);

            if (lastPheromone == null) {
                trailStrategy.onTrailEnd(ant, null);
                return;
            }
        }

        // 2. Target Management - check if we reached current target
        if (currentTarget != null && ant.getPosition().dst2(getCenterPos(currentTarget)) < reachedThresholdSq) {
            lastPheromone = currentTarget;
            currentTarget = null;
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

    private Vector2 getCenterPos(Pheromone p) {
        return converter.pheromoneGridToWorld(p.getPosition());
    }

    @Override
    public void handleCollision() {
        currentTarget = null;
    }
}
