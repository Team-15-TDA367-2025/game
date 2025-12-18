package se.chalmers.tda367.team15.game.model.entity.ant.behavior.trail;

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
    // Protected because it's also used in the PatrolTrailStrategy
    protected final Random random = new Random();

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
    protected Pheromone moveRandomlyOnTrail(List<Pheromone> neighbors, Pheromone current) {
        if (neighbors.isEmpty())
            return null;

        // Try forward direction; if dead end flip once
        List<Pheromone> forward = filterByDistance(neighbors, current, outwards);
        if (forward.isEmpty()) {
            outwards = !outwards;
            forward = filterByDistance(neighbors, current, outwards);
        }

        if (!forward.isEmpty()) {
            return forward.get(random.nextInt(forward.size()));
        }

        // Still nothing: pick any neighbor not equal to current
        if (current == null) {
            return neighbors.get(random.nextInt(neighbors.size()));
        }

        var curPos = current.getPosition();
        // Filter to neighbors that are actually different from current
        List<Pheromone> validNeighbors = neighbors.stream()
                .filter(p -> !p.getPosition().equals(curPos))
                .toList();

        if (!validNeighbors.isEmpty()) {
            return validNeighbors.get(random.nextInt(validNeighbors.size()));
        }

        // Truly stuck: only neighbor is current cell
        return null;
    }

}
