package se.chalmers.tda367.team15.game.model.entity.ant.behavior.trail;

import java.util.Comparator;
import java.util.List;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.WanderBehavior;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.managers.PheromoneManager;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;

/**
 * Trail strategy for scout ants following EXPLORE pheromones.
 * - Outward: Always prefer higher distance (outward exploration)
 * - At trail end: Switch to returning mode
 * - Returning: Prefer lower distance AND destroy pheromones behind
 */
public class ExploreTrailStrategy implements TrailStrategy {

    private static final float SPEED_MULTIPLIER = 1.3f;

    private boolean returning = false;
    private Pheromone lastPheromone = null;

    @Override
    public Pheromone selectNextPheromone(Ant ant, List<Pheromone> neighbors, Pheromone current) {
        if (neighbors.isEmpty()) {
            return null;
        }

        int currentDistance = current != null ? current.getDistance() : 0;

        if (!returning) {
            // Going outward - prefer higher distance
            List<Pheromone> higherDistance = neighbors.stream()
                    .filter(p -> p.getDistance() > currentDistance)
                    .toList();

            if (higherDistance.isEmpty()) {
                // Reached trail end - switch to returning
                returning = true;
                lastPheromone = current;
                return selectReturnPheromone(neighbors, currentDistance);
            }

            // Pick highest distance pheromone
            return higherDistance.stream()
                    .max(Comparator.comparingInt(Pheromone::getDistance))
                    .orElse(higherDistance.get(0));
        } else {
            // Returning - prefer lower distance
            return selectReturnPheromone(neighbors, currentDistance);
        }
    }

    private Pheromone selectReturnPheromone(List<Pheromone> neighbors, int currentDistance) {
        List<Pheromone> lowerDistance = neighbors.stream()
                .filter(p -> p.getDistance() < currentDistance)
                .toList();

        if (lowerDistance.isEmpty()) {
            // Back at start - return null to trigger onTrailEnd
            return null;
        }

        return lowerDistance.stream()
                .min(Comparator.comparingInt(Pheromone::getDistance))
                .orElse(lowerDistance.get(0));
    }

    @Override
    public void onTrailEnd(Ant ant, Pheromone current, PheromoneManager pheromoneManagerParam,
            Home homeParam, EntityQuery entityQueryParam, PheromoneGridConverter converterParam,
            TrailStrategy strategy) {

        if (returning) {
            // Destroy the current pheromone when returning
            if (current != null) {
                pheromoneManagerParam.removePheromone(current.getPosition());
            }
            // Also destroy the last pheromone we came from
            if (lastPheromone != null && lastPheromone != current) {
                pheromoneManagerParam.removePheromone(lastPheromone.getPosition());
            }
        } else {
            // Just reached the end for the first time - start returning
            returning = true;
            lastPheromone = current;
        }

        // Switch to wander behavior
        ant.setBehavior(
                new WanderBehavior(ant, homeParam, entityQueryParam, converterParam, strategy, pheromoneManagerParam));
    }

    @Override
    public float getSpeedMultiplier() {
        return SPEED_MULTIPLIER;
    }
}
