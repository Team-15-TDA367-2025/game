package se.chalmers.tda367.team15.game.model.entity.ant.behavior.trail;

import java.util.List;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.FollowTrailBehavior;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;

/**
 * Trail strategy for scout ants following EXPLORE pheromones.
 * - Walk outward (higher distance) until trail end
 * - At trail end: leave trail and start wandering (return null)
 */
public class ExploreTrailStrategy extends TrailStrategy {

    private static final float SPEED_MULTIPLIER = 1.3f;

    @Override
    public Pheromone selectNextPheromone(Ant ant, List<Pheromone> neighbors,
            Pheromone current, FollowTrailBehavior behavior) {
        if (neighbors.isEmpty()) {
            return null;
        }

        // Always try to move outward (higher distance)
        List<Pheromone> outward = filterByDistance(neighbors, current, true);

        if (outward.isEmpty()) {
            // Reached trail end - leave trail to start wandering
            return null;
        }

        // Filter out current position from the outward list
        List<Pheromone> options = current != null
                ? outward.stream().filter(p -> !p.getPosition().equals(current.getPosition())).toList()
                : outward;

        if (options.isEmpty()) {
            return null;
        }

        return options.get(random.nextInt(options.size()));
    }

    @Override
    public void onTrailEnd(Ant ant, Pheromone current, FollowTrailBehavior behavior) {
        ant.setWanderBehaviour(true);
    }

    @Override
    public float getSpeedMultiplier() {
        return SPEED_MULTIPLIER;
    }
}
