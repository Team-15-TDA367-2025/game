package se.chalmers.tda367.team15.game.model.entity.ant.behavior.trail;

import java.util.List;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;

/**
 * Trail strategy for soldier ants following ATTACK pheromones.
 * Spreading behavior:
 * - Patrol along the trail
 * - Random chance to turn around when other soldiers are on the same cell
 * - Turn around at trail ends
 */
public class PatrolTrailStrategy extends TrailStrategy {

    private static final float SPEED_MULTIPLIER = 1.0f;
    private static final float TURN_CHANCE_PER_SOLDIER = 0.05f; // 5% per soldier
    private static final float MAX_TURN_CHANCE = 0.20f; // Cap at 20%

    @Override
    public Pheromone selectNextPheromone(Ant ant, List<Pheromone> neighbors, Pheromone current) {
        if (neighbors.isEmpty()) {
            return null;
        }

        // Get soldier count from current pheromone (O(1) instead of O(n))
        // Subtract 1 because the count includes this ant
        int otherSoldiers = current != null ? Math.max(0, current.getAntCount() - 1) : 0;

        // Turn chance scales with count, capped to prevent chaos
        if (otherSoldiers > 0) {
            float turnChance = Math.min(MAX_TURN_CHANCE, TURN_CHANCE_PER_SOLDIER * otherSoldiers);
            if (random.nextInt(1000) < turnChance * 1000) {
                outwards = !outwards;
            }
        }

        return moveRandomlyOnTrail(neighbors, current);
    }

    @Override
    public void onTrailEnd(Ant ant, Pheromone current) {
        if (current == null) {
            ant.setWanderBehaviour(true);
        }
        outwards = !outwards;
    }

    @Override
    public float getSpeedMultiplier() {
        return SPEED_MULTIPLIER;
    }
}
