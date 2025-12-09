package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;

public class FollowTrailBehavior extends AntBehavior {
    private final PheromoneType allowedType;
    private final boolean stickyTrail;

    private static final float SPEED_BOOST_ON_TRAIL = 1.5f;
    // Threshold as fraction of pheromone cell size (must be < 1 to avoid reaching
    // multiple cells)
    private static final float REACHED_THRESHOLD_FRACTION = 0.3f;

    private boolean returningToColony = false;
    private Pheromone lastPheromone = null;
    private Pheromone currentTarget = null;
    private float reachedThresholdSq;

    public FollowTrailBehavior(Ant ant) {
        super(ant);
        // Calculate threshold based on pheromone cell size
        float cellSize = ant.getSystem().getConverter().getPheromoneCellSize();
        float threshold = cellSize * REACHED_THRESHOLD_FRACTION;
        this.reachedThresholdSq = threshold * threshold;

        String typeId = ant.getType().id();
        switch (typeId) {
            case "scout" -> {
                allowedType = PheromoneType.EXPLORE;
                stickyTrail = false;
            }
            case "soldier" -> {
                allowedType = PheromoneType.ATTACK;
                stickyTrail = false;
            }
            case "worker" -> {
                allowedType = PheromoneType.GATHER;
                stickyTrail = true;
            }
            default -> {
                allowedType = PheromoneType.EXPLORE;
                stickyTrail = false;
            }
        }
    }

    @Override
    public void update(PheromoneSystem system, float deltaTime) {
        if (enemiesInSight()) {
            ant.setBehavior(new AttackBehavior(ant, ant.getPosition()));
            return;
        }

        List<Pheromone> neighbors = system.getPheromonesIn3x3(ant.getGridPosition()).stream()
                .filter(p -> p.getType() == allowedType)
                .collect(Collectors.toList());

        // 1. Initialization / Re-anchoring
        if (lastPheromone == null) {
            lastPheromone = neighbors.stream()
                    .min(Comparator.comparingInt(Pheromone::getDistance))
                    .orElse(null);

            if (lastPheromone == null) {
                ant.setBehavior(new WanderBehavior(ant));
                return;
            }
        }

        // 2. Target Management
        if (currentTarget != null && ant.getPosition().dst2(getCenterPos(currentTarget)) < reachedThresholdSq) {
            lastPheromone = currentTarget;
            currentTarget = null;
        }

        if (currentTarget == null) {
            // Try current direction, then flip if blocked
            currentTarget = findNextPheromone(neighbors, returningToColony);
            if (currentTarget == null) {
                returningToColony = !returningToColony;
                currentTarget = findNextPheromone(neighbors, returningToColony);
            }

            // If still no target, we lost the trail
            if (currentTarget == null) {
                ant.setBehavior(new WanderBehavior(ant));
                return;
            }
        }

        // 3. Movement
        Vector2 targetPos = getCenterPos(currentTarget);
        Vector2 diff = new Vector2(targetPos).sub(ant.getPosition());
        float distSq = diff.len2();

        if (distSq > 0.001f) {
            // Scale speed based on distance to avoid overshooting
            float maxSpeed = ant.getSpeed() * SPEED_BOOST_ON_TRAIL;
            float cellSize = ant.getSystem().getConverter().getPheromoneCellSize();
            // Slow down when close to target to prevent overshooting
            float speed = Math.min(maxSpeed, Math.max(ant.getSpeed(), (float) Math.sqrt(distSq) / cellSize * maxSpeed));
            ant.setVelocity(diff.nor().scl(speed));
        } else {
            // Very close to target - stop to prevent oscillation
            ant.setVelocity(new Vector2(0, 0));
        }

    }

    private Pheromone findNextPheromone(List<Pheromone> neighbors, boolean returning) {
        int currentDist = lastPheromone.getDistance();

        List<Pheromone> candidates = neighbors.stream()
                .filter(p -> !p.getPosition().equals(lastPheromone.getPosition()))
                .filter(p -> returning ? p.getDistance() < currentDist : p.getDistance() > currentDist)
                .collect(Collectors.toList());

        if (candidates.isEmpty())
            return null;

        Collections.shuffle(candidates);
        candidates.sort((p1, p2) -> returning
                ? Integer.compare(p2.getDistance(), p1.getDistance())
                : Integer.compare(p1.getDistance(), p2.getDistance()));

        return candidates.get(0);
    }

    private Vector2 getCenterPos(Pheromone p) {
        // Convert pheromone grid position to world position (center of cell)
        PheromoneGridConverter converter = ant.getSystem().getConverter();
        return converter.pheromoneGridToWorld(p.getPosition());
    }
}
