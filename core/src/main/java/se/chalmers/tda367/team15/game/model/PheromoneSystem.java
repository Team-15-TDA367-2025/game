package se.chalmers.tda367.team15.game.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.math.GridPoint2;

public class PheromoneSystem {
    private final PheromoneGrid pheromoneGrid;
    private final GridPoint2 colonyPosition;

    public PheromoneSystem(GridPoint2 colonyPosition) {
        this.pheromoneGrid = new PheromoneGrid();
        this.colonyPosition = colonyPosition;
    }

    /**
     * Adds a pheromone at the specified position if it's valid.
     * A position is valid if it's adjacent or diagonal to the colony or an existing pheromone.
     * The distance is set based on the lowest distance pheromone in a 3x3 area (excluding center).
     * @param pos The position to add the pheromone
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
     * Finds the lowest distance in adjacent cells (up, down, left, right) around the position.
     * Checks both existing pheromones and the colony position.
     * @param centerPos The center position
     * @return The lowest distance found, or -1 if no valid parent exists
     */
    private int findLowestNeighbor(GridPoint2 centerPos) {
        int minDistance = Integer.MAX_VALUE;
        boolean foundValidParent = false;

        // Check only strictly adjacent cells (cross shape)
        int[][] offsets = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        
        for (int[] offset : offsets) {
            GridPoint2 neighborPos = new GridPoint2(centerPos.x + offset[0], centerPos.y + offset[1]);
            
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
     * Removes all subsequent pheromones in the trail with strictly higher distance.
     * The pheromone at the clicked position is not removed, only those after it.
     * @param pos The position where deletion starts
     */
    public void removePheromone(GridPoint2 pos) {
        Pheromone pheromone = pheromoneGrid.getPheromoneAt(pos);
        if (pheromone == null) {
            return;
        }

        int targetDistance = pheromone.getDistance();
        // Remove all pheromones with distance > targetDistance that are connected
        // Start from strict neighbors
        int[][] offsets = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] offset : offsets) {
            GridPoint2 neighbor = new GridPoint2(pos.x + offset[0], pos.y + offset[1]);
            removeConnectedPheromones(neighbor, targetDistance);
        }
    }

    /**
     * Recursively removes connected pheromones with distance > targetDistance.
     */
    private void removeConnectedPheromones(GridPoint2 pos, int targetDistance) {
        Pheromone pheromone = pheromoneGrid.getPheromoneAt(pos);
        if (pheromone == null || pheromone.getDistance() <= targetDistance) {
            return;
        }

        pheromoneGrid.removePheromone(pos);

        // Check strict neighbors
        int[][] offsets = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] offset : offsets) {
            GridPoint2 neighbor = new GridPoint2(pos.x + offset[0], pos.y + offset[1]);
            removeConnectedPheromones(neighbor, targetDistance);
        }
    }


    /**
     * Gets all pheromones for rendering.
     */
    public Collection<Pheromone> getPheromones() {
        return pheromoneGrid.getAllPheromones();
    }

    /**
     * Gets the pheromone at the specified grid position, or null if none exists.
     */
    public Pheromone getPheromoneAt(GridPoint2 gridPos) {
        return pheromoneGrid.getPheromoneAt(gridPos);
    }

    /**
     * Gets the colony position.
     */
    public GridPoint2 getColonyPosition() {
        return colonyPosition;
    }

    /**
     * Gets all pheromones in the 3x3 area around the center (including diagonals and center).
     */
    public List<Pheromone> getPheromonesIn3x3(GridPoint2 centerGridPos) {
        return pheromoneGrid.getPheromonesIn3x3(centerGridPos);
    }
}
