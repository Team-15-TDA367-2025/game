package se.chalmers.tda367.team15.game.model.entity.ant.behavior.trail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;

/**
 * Abstract base class for how ants follow pheromone trails.
 * Different ant types use different strategies based on their role.
 */
public abstract class TrailStrategy {

    protected boolean outwards = true;

    /**
     * Selects the next pheromone to move toward from available neighbors.
     *
     * @param ant       The ant making the decision
     * @param neighbors Available pheromones in adjacent cells
     * @param current   The current pheromone the ant is on (may be null)
     * @return The selected pheromone to move toward, or null to leave the trail
     */
    public abstract Pheromone selectNextPheromone(Ant ant, List<Pheromone> neighbors, Pheromone current);

    /**
     * Called when the ant reaches a trail end (no valid next pheromone).
     *
     * @param ant     The ant at the trail end
     * @param current The current pheromone
     */
    public abstract void onTrailEnd(Ant ant, Pheromone current);

    /**
     * @return Speed multiplier when following this trail type
     */
    public abstract float getSpeedMultiplier();

    public boolean isOutwards() {
        return outwards;
    }

    public void setOutwards(boolean outwards) {
        this.outwards = outwards;
    }

    /**
     * Helper to get pheromones that are further away or closer to the colony.
     *
     * @param neighbors Available neighbors
     * @param current   Current pheromone (may be null)
     * @param higher    Dist true for further away, false for closer
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
     * Helper for wandering randomly on a trail with automatic turn-around at dead
     * ends.
     * Picks a random pheromone in the current direction, turning around if needed.
     *
     * @param neighbors Available neighbor pheromones
     * @param current   Current pheromone (may be null)
     * @param random    Random instance for shuffling
     * @return Selected pheromone, or null if completely stuck
     */
    protected Pheromone moveRandomlyOnTrail(List<Pheromone> neighbors, Pheromone current, Random random) {
        List<Pheromone> forward = filterByDistance(neighbors, current, outwards);

        if (forward.isEmpty()) {
            // Dead end - turn around
            outwards = !outwards;
            forward = filterByDistance(neighbors, current, outwards);
        }

        if (forward.isEmpty()) {
            // Still nothing - pick any neighbor that isn't current
            return neighbors.stream()
                    .filter(p -> current == null || !p.getPosition().equals(current.getPosition()))
                    .findAny()
                    .orElse(neighbors.isEmpty() ? null : neighbors.get(0));
        }

        // Pick randomly from forward options
        List<Pheromone> shuffled = new ArrayList<>(forward);
        Collections.shuffle(shuffled, random);
        return shuffled.get(0);
    }
}
