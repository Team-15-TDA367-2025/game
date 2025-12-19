package se.chalmers.tda367.team15.game.model.entity.ant.behavior.trail;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.FollowTrailBehavior;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;

/**
 * Abstract base class for how ants follow pheromone trails.
 * Different ant types use different strategies based on their role.
 * 
 * NOTE: TrailStrategy instances are shared between all ants of the same type.
 * Per-ant state (like direction) must be stored in FollowTrailBehavior.
 */
public abstract class TrailStrategy {

    protected final Random random = new Random();

    /**
     * Selects the next pheromone to move toward from available neighbors.
     *
     * @param ant       The ant making the decision
     * @param neighbors Available pheromones in adjacent cells
     * @param current   The current pheromone the ant is on (may be null)
     * @param behavior  The ant's behavior context (holds per-ant state)
     * @return The selected pheromone to move toward, or null to leave the trail
     */
    public abstract Pheromone selectNextPheromone(Ant ant, List<Pheromone> neighbors,
            Pheromone current, FollowTrailBehavior behavior);

    /**
     * Called when the ant reaches a trail end (no valid next pheromone).
     *
     * @param ant      The ant at the trail end
     * @param current  The current pheromone
     * @param behavior The ant's behavior context
     */
    public abstract void onTrailEnd(Ant ant, Pheromone current, FollowTrailBehavior behavior);

    /**
     * @return Speed multiplier when following this trail type
     */
    public abstract float getSpeedMultiplier();

    /**
     * Helper to get pheromones that are further away or closer to the colony.
     * Uses STRICT inequality - does not include same-distance pheromones.
     *
     * @param neighbors Available neighbors
     * @param current   Current pheromone (may be null)
     * @param higher    true for further away, false for closer
     * @return List of matching pheromones
     */
    protected List<Pheromone> filterByDistance(List<Pheromone> neighbors, Pheromone current, boolean higher) {
        int currentDist = current != null ? current.getDistance() : (higher ? -1 : Integer.MAX_VALUE);
        return neighbors.stream()
                .filter(p -> higher ? p.getDistance() > currentDist : p.getDistance() < currentDist)
                .toList();
    }

    /**
     * Helper to select the pheromone with the best distance.
     *
     * @param options Candidates
     * @param max     True for highest distance, false for lowest
     * @return The best pheromone or null if empty
     */
    protected Pheromone getBestByDistance(List<Pheromone> options, boolean max) {
        if (options.isEmpty()) {
            return null;
        }
        Comparator<Pheromone> comp = Comparator.comparingInt(Pheromone::getDistance);
        return options.stream()
                .max(max ? comp : comp.reversed())
                .orElse(options.get(0));
    }

    /**
     * Picks a random pheromone in the current direction, turning around at dead
     * ends.
     * 
     * SIMPLE ALGORITHM:
     * 1. Pick the BEST pheromone in desired direction (highest if outwards, lowest
     * if inwards)
     * 2. If that's the cell we're on -> dead end, turn around
     * 3. Otherwise, move there
     *
     * @param neighbors Available neighbor pheromones (including current)
     * @param current   Current pheromone the ant is on (may be null)
     * @param behavior  The ant's behavior context (holds outwards state)
     * @return Selected pheromone to move toward, or null if stuck
     */
    protected Pheromone moveRandomlyOnTrail(List<Pheromone> neighbors, Pheromone current,
            FollowTrailBehavior behavior) {
        if (neighbors.isEmpty()) {
            return null;
        }

        // Filter to pheromones in the forward direction (excluding current position)
        List<Pheromone> forward = neighbors.stream()
                .filter(p -> current == null || !p.getPosition().equals(current.getPosition()))
                .filter(p -> {
                    if (current == null)
                        return true;
                    int currentDist = current.getDistance();
                    return behavior.isOutwards()
                            ? p.getDistance() > currentDist
                            : p.getDistance() < currentDist;
                })
                .toList();

        // If no forward options, we're at a dead end - turn around
        if (forward.isEmpty()) {
            System.out.println("Dead end - turning around. CurrentDist=" +
                    (current != null ? current.getDistance() : "null") +
                    ", outwards=" + behavior.isOutwards());
            behavior.flipDirection();

            // Find options in the new direction
            forward = neighbors.stream()
                    .filter(p -> current == null || !p.getPosition().equals(current.getPosition()))
                    .filter(p -> {
                        if (current == null)
                            return true;
                        int currentDist = current.getDistance();
                        return behavior.isOutwards()
                                ? p.getDistance() > currentDist
                                : p.getDistance() < currentDist;
                    })
                    .toList();
        }

        if (forward.isEmpty()) {
            // No options in either direction - just stay put or pick any neighbor
            return current;
        }

        // Pick RANDOMLY from forward options (for intersection variety)
        return forward.get(random.nextInt(forward.size()));
    }
}
