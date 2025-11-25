package se.chalmers.tda367.team15.game.model;

import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.math.GridPoint2;

public class PheromoneSystem {
    private static final int[][] NEIGHBOR_OFFSETS = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };

    private final PheromoneGrid pheromoneGrid;
    private final GridPoint2 colonyPosition;

    public PheromoneSystem(GridPoint2 colonyPosition) {
        this.pheromoneGrid = new PheromoneGrid();
        this.colonyPosition = colonyPosition;
    }

    /**
     * Adds a pheromone at the specified position if it's valid.
     * A position is valid if it's adjacent or diagonal to the colony or an existing
     * pheromone.
     * The distance is set based on the lowest distance pheromone in a 3x3 area
     * (excluding center).
     * 
     * @param pos  The position to add the pheromone
     * @param type The type of pheromone
     * @return true if the pheromone was added, false if the position was invalid
     */
    public boolean addPheromone(GridPoint2 pos, PheromoneType type) {
        // Check if position already has a pheromone
        if (pheromoneGrid.hasPheromoneAt(pos)) {
            return false;
        }

        // Find the lowest distance in a 3x3 area around the position (excluding center)
        int minDistance = findLowestNeighbor(pos);
        if (minDistance == -1) {
            // No valid parent found (not adjacent to colony or any pheromone)
            return false;
        }

        // Create new pheromone with distance = minDistance + 1
        Pheromone pheromone = new Pheromone(pos, type, minDistance + 1);
        pheromoneGrid.addPheromone(pheromone);
        return true;
    }

    /**
     * Finds the lowest distance in adjacent cells (up, down, left, right) around
     * the position.
     * Checks both existing pheromones and the colony position.
     * 
     * @param pos The center position
     * @return The lowest distance found, or -1 if no valid parent exists
     */
    private int findLowestNeighbor(GridPoint2 pos) {
        int minDistance = Integer.MAX_VALUE;
        boolean foundValidParent = false;

        // Check only strictly adjacent cells (cross shape)
        for (int[] offset : NEIGHBOR_OFFSETS) {
            GridPoint2 neighborPos = new GridPoint2(pos.x + offset[0], pos.y + offset[1]);

            // Check if this is the colony position
            if (neighborPos.equals(colonyPosition)) {
                minDistance = 0; // Colony has distance 0
                foundValidParent = true;
            } else {
                // Check if there's a pheromone at this position
                Pheromone pheromone = pheromoneGrid.getPheromoneAt(neighborPos);
                if (pheromone != null && pheromone.getDistance() < minDistance) {
                    minDistance = pheromone.getDistance();
                    foundValidParent = true;
                }
            }
        }

        return foundValidParent ? minDistance : -1;
    }

    /**
     * Removes the pheromone at the specified position and all subsequent pheromones
     * in the trail with strictly higher distance.
     * 
     * @param pos The position where deletion starts
     */
    public void removePheromone(GridPoint2 pos) {
        Pheromone pheromone = pheromoneGrid.getPheromoneAt(pos);
        if (pheromone == null) {
            return;
        }
        int targetDistance = pheromone.getDistance();
        pheromoneGrid.removePheromone(pos);
        removePheromone(pos, targetDistance);
    }

    /** Recurse and remove all pheromones with distance greater than minDistance. */
    private void removePheromone(GridPoint2 pos, int minDistance) {
        for (int[] offset : NEIGHBOR_OFFSETS) {
            GridPoint2 neighbor = new GridPoint2(pos.x + offset[0], pos.y + offset[1]);

            Pheromone pheromone = pheromoneGrid.getPheromoneAt(neighbor);

            if (pheromone != null && pheromone.getDistance() > minDistance) {
                pheromoneGrid.removePheromone(neighbor);
                removePheromone(neighbor, minDistance);
            }
        }
    }

    public Collection<Pheromone> getPheromones() {
        return pheromoneGrid.getAllPheromones();
    }

    public Pheromone getPheromoneAt(GridPoint2 gridPos) {
        return pheromoneGrid.getPheromoneAt(gridPos);
    }

    public List<Pheromone> getPheromonesIn3x3(GridPoint2 centerGridPos) {
        return pheromoneGrid.getPheromonesIn3x3(centerGridPos);
    }
}
