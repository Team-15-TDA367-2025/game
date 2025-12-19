package se.chalmers.tda367.team15.game.model.entity.ant.behavior.trail;

import java.util.List;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;

/**
 * Trail strategy for worker ants following GATHER pheromones.
 * - Walk forward (pick randomly from all higher-distance options)
 * - Turn around at dead ends (don't leave trail)
 * - Only return home when inventory is full
 */
public class GatherTrailStrategy extends TrailStrategy {

    private static final float SPEED_MULTIPLIER = 1.2f;

    @Override
    public Pheromone selectNextPheromone(Ant ant, List<Pheromone> neighbors, Pheromone current) {
        if (neighbors.isEmpty()) {
            return null;
        }

        if (ant.getInventory().isFull()) {
            outwards = false;
            List<Pheromone> homeward = filterByDistance(neighbors, current, false);
            if (homeward.isEmpty()) {
                return null;
            }
            return getBestByDistance(homeward, false);
        }

        return moveRandomlyOnTrail(neighbors, current);
    }

    @Override
    public void onTrailEnd(Ant ant, Pheromone current) {
        // Worker ants should stay on trail, but if somehow off trail, wander
        ant.setWanderBehaviour(true);
    }

    @Override
    public float getSpeedMultiplier() {
        return SPEED_MULTIPLIER;
    }
}
