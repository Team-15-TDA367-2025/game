package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.Pheromone;
import se.chalmers.tda367.team15.game.model.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;

public class FollowTrailBehavior implements AntBehavior {
    private boolean returningToColony = false;
    private Pheromone lastPheromone = null;
    private Pheromone currentTarget = null;
    private static final float SPEED_BOOST_ON_TRAIL = 2f;
    private static final float REACHED_THRESHOLD = 0.3f;

    @Override
    public void update(Ant ant, PheromoneSystem system, float deltaTime) {
        GridPoint2 gridPos = ant.getGridPosition();
        List<Pheromone> neighbors = system.getPheromonesIn3x3(gridPos);
        if (lastPheromone == null) {
            lastPheromone = neighbors.stream().min(Comparator.comparingInt(Pheromone::getDistance)).orElse(null);
        }

        if (lastPheromone == null || neighbors.isEmpty()) {
            ant.setBehavior(new WanderBehavior());
            return;
        }

        // Check if we've reached our current target
        if (currentTarget != null) {
            Vector2 currentPos = ant.getPosition();
            Vector2 targetPos = new Vector2(currentTarget.getPosition().x + 0.5f, currentTarget.getPosition().y + 0.5f);
            float distSq = currentPos.dst2(targetPos);

            if (distSq < REACHED_THRESHOLD * REACHED_THRESHOLD) {
                // We've reached the target, update lastPheromone and clear current target
                lastPheromone = currentTarget;
                currentTarget = null;

                boolean canProgress = neighbors.stream()
                        .filter(p -> !p.getPosition().equals(gridPos)
                                && !p.getPosition().equals(lastPheromone.getPosition()))
                        .anyMatch(p -> getComparator().compare(p, lastPheromone) < 0);

                if (!canProgress) {
                    returningToColony = !returningToColony;
                }

            }
        }

        // If we don't have a current target, find a new one
        if (currentTarget == null) {
            Pheromone nextPheromone = findNextPheromone(neighbors, gridPos);
            if (nextPheromone == null) {
                ant.setBehavior(new WanderBehavior());
                return;
            }
            currentTarget = nextPheromone;
        }

        // Move towards the current target
        Vector2 targetPos = new Vector2(currentTarget.getPosition().x + 0.5f, currentTarget.getPosition().y + 0.5f);
        Vector2 currentPos = ant.getPosition();
        Vector2 direction = targetPos.cpy().sub(currentPos);

        // Set velocity towards target
        if (direction.len2() > 0.01f) {
            direction.nor();
            float speed = ant.getSpeed() * SPEED_BOOST_ON_TRAIL;
            ant.setVelocity(direction.scl(speed));
        } else {
            // Very close, just update and find next
            lastPheromone = currentTarget;
            currentTarget = null;
        }
    }

    private Comparator<Pheromone> getComparator() {
        if (!returningToColony) {
            return Comparator.comparingInt(Pheromone::getDistance).reversed();
        }
        return Comparator.comparingInt(Pheromone::getDistance);
    }

    private Pheromone findNextPheromone(List<Pheromone> neighbors, GridPoint2 currentGridPos) {
        Comparator<Pheromone> comparator = getComparator();
        PriorityQueue<Pheromone> priorityQueue = new PriorityQueue<>(comparator);

        Collections.shuffle(neighbors);
        priorityQueue.addAll(neighbors);

        // Get the best pheromone (closest/farthest depending on direction)
        Pheromone best = priorityQueue.poll();

        if (best == null) {
            return null;
        }

        while (best.getDistance() == lastPheromone.getDistance() && !priorityQueue.isEmpty()) {
            best = priorityQueue.poll();
        }

        return best;
    }
}
