package se.chalmers.tda367.team15.game.model.entity.ant.behavior.trail;

import java.util.List;
import java.util.Random;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;

/**
 * Trail strategy for soldier ants following ATTACK pheromones.
 * Spreading behavior:
 * - Patrol along the trail
 * - Random chance to turn around when seeing soldiers with lower hashCode
 * - Turn around at trail ends
 * hashCode priority ensures consistent ordering so not everyone turns at once.
 */
public class PatrolTrailStrategy extends TrailStrategy {

    private static final float SPEED_MULTIPLIER = 1.0f;
    private static final float TURN_CHANCE_PER_SOLDIER = 0.05f; // 5% per soldier
    private static final float MAX_TURN_CHANCE = 0.20f; // Cap at 20%
    private static final float PROXIMITY_CELLS = 1.5f; // React to soldiers within this many pheromone cells

    private final EntityQuery entityQuery;
    private final PheromoneGridConverter converter;
    private final Random random = new Random();

    public PatrolTrailStrategy(EntityQuery entityQuery, PheromoneGridConverter converter) {
        this.entityQuery = entityQuery;
        this.converter = converter;
    }

    @Override
    public Pheromone selectNextPheromone(Ant ant, List<Pheromone> neighbors, Pheromone current) {
        if (neighbors.isEmpty()) {
            return null;
        }

        // Use a small proximity radius (about 1.5 pheromone cells) instead of vision
        float proximityRadius = converter.getPheromoneCellSize() * PROXIMITY_CELLS;

        // Count nearby soldiers with lower hashCode (they have "priority")
        long prioritySoldiers = entityQuery.getEntitiesOfType(Ant.class).stream()
                .filter(a -> a != ant)
                .filter(a -> a.getType().allowedPheromones().contains(PheromoneType.ATTACK))
                .filter(a -> a.getPosition().dst(ant.getPosition()) <= proximityRadius)
                .filter(a -> a.hashCode() < ant.hashCode())
                .count();

        // Turn chance scales with count, capped to prevent chaos
        if (prioritySoldiers > 0) {
            float turnChance = Math.min(MAX_TURN_CHANCE, TURN_CHANCE_PER_SOLDIER * prioritySoldiers);
            if (random.nextFloat() < turnChance) {
                outwards = !outwards;
            }
        }

        return moveRandomlyOnTrail(neighbors, current, random);
    }

    @Override
    public void onTrailEnd(Ant ant, Pheromone current) {
        if (current == null) {
            ant.setWanderBehaviour();
        }
        outwards = !outwards;
    }

    @Override
    public float getSpeedMultiplier() {
        return SPEED_MULTIPLIER;
    }
}
