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
 * Simple spreading behavior:
 * - Patrol along the trail
 * - Random chance to turn around when seeing another soldier
 * - Turn around at trail ends
 */
public class AttackTrailStrategy extends TrailStrategy {

    private static final float SPEED_MULTIPLIER = 1.0f;
    private static final float TURN_CHANCE_PER_SOLDIER = 0.05f; // 5% per visible soldier per tick

    private final EntityQuery entityQuery;
    private final Random random = new Random();

    public AttackTrailStrategy(EntityQuery entityQuery, PheromoneGridConverter converter) {
        this.entityQuery = entityQuery;
    }

    @Override
    public Pheromone selectNextPheromone(Ant ant, List<Pheromone> neighbors, Pheromone current) {
        if (neighbors.isEmpty()) {
            return null;
        }

        // Count visible soldiers with LOWER hashCode (they have "priority")
        // This creates consistent ordering - we yield to ants with lower hashCode
        long prioritySoldiersCount = entityQuery.getEntitiesOfType(Ant.class).stream()
                .filter(a -> a != ant)
                .filter(a -> a.getType().allowedPheromones().contains(PheromoneType.ATTACK))
                .filter(a -> a.getPosition().dst(ant.getPosition()) <= ant.getVisionRadius())
                .filter(a -> a.hashCode() < ant.hashCode()) // Only yield to lower hashCode ants
                .count();

        // Turn chance scales with number of priority soldiers
        if (prioritySoldiersCount > 0) {
            float turnChance = TURN_CHANCE_PER_SOLDIER * prioritySoldiersCount;
            if (random.nextFloat() < turnChance) {
                outwards = !outwards;
            }
        }

        // Move along trail in current direction
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
