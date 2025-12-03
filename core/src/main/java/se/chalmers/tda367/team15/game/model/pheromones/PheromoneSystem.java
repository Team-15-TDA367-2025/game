package se.chalmers.tda367.team15.game.model.pheromones;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

import com.badlogic.gdx.math.GridPoint2;

public class PheromoneSystem {
    private static final int[][] NEIGHBOR_OFFSETS = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };

    private final PheromoneGrid pheromoneGrid;
    private final GridPoint2 colonyPosition;
    private final PheromoneGridConverter converter;

    public PheromoneSystem(GridPoint2 colonyPosition, PheromoneGridConverter converter) {
        this.pheromoneGrid = new PheromoneGrid();
        this.colonyPosition = colonyPosition;
        this.converter = converter;
    }

    public PheromoneGridConverter getConverter() {
        return converter;
    }

    /**
     * Adds a pheromone at the specified position if it's valid.
     * A position is valid if it's adjacent or diagonal to the colony or an existing
     * pheromone.
     * 
     * @param pos  The position to add the pheromone
     * @param type The type of pheromone
     * @return true if the pheromone was added, false if the position was invalid
     */
    public boolean addPheromone(GridPoint2 pos, PheromoneType type) {
        if (pheromoneGrid.hasPheromoneAt(pos)) {
            return false;
        }

        int minDistance = findLowestNeighbor(pos);
        if (minDistance == -1) {
            // No valid parent found (not adjacent to colony or any pheromone)
            return false;
        }

        Pheromone pheromone = new Pheromone(pos, type, minDistance + 1);
        pheromoneGrid.addPheromone(pheromone);

        // Update connected pheromones that now have a shorter path via this new pheromone
        propagateShorterDistances(pos, pheromone.getDistance());

        return true;
    }

    /**
     * Propagates shorter distances to all reachable pheromones using BFS.
     * Each node is processed at most once.
     * 
     * @param startPos      The position from which to start propagation
     * @param startDistance The distance at the starting position
     */
    private void propagateShorterDistances(GridPoint2 startPos, int startDistance) {
        Deque<GridPoint2> queue = new ArrayDeque<>();
        queue.add(startPos);

        while (!queue.isEmpty()) {
            GridPoint2 pos = queue.poll();
            Pheromone current = pheromoneGrid.getPheromoneAt(pos);
            if (current == null) {
                continue;
            }
            int currentDistance = current.getDistance();

            for (int[] offset : NEIGHBOR_OFFSETS) {
                GridPoint2 neighborPos = new GridPoint2(pos.x + offset[0], pos.y + offset[1]);
                Pheromone neighbor = pheromoneGrid.getPheromoneAt(neighborPos);

                if (neighbor != null && neighbor.getDistance() > currentDistance + 1) {
                    Pheromone updated = new Pheromone(neighborPos, neighbor.getType(), currentDistance + 1);
                    pheromoneGrid.addPheromone(updated);
                    queue.add(neighborPos);
                }
            }
        }
    }

    /**
     * Removes all pheromones downstream of the given position using BFS.
     * 
     * @param startPos    The position from which to start removal
     * @param minDistance Pheromones with distance > minDistance will be removed
     */
    private void propagateRemoval(GridPoint2 startPos, int minDistance) {
        Deque<GridPoint2> queue = new ArrayDeque<>();
        queue.add(startPos);

        while (!queue.isEmpty()) {
            GridPoint2 pos = queue.poll();

            for (int[] offset : NEIGHBOR_OFFSETS) {
                GridPoint2 neighborPos = new GridPoint2(pos.x + offset[0], pos.y + offset[1]);
                Pheromone neighbor = pheromoneGrid.getPheromoneAt(neighborPos);

                if (neighbor != null && neighbor.getDistance() > minDistance) {
                    pheromoneGrid.removePheromone(neighborPos);
                    queue.add(neighborPos);
                }
            }
        }
    }

    /**
     * Finds the lowest distance in adjacent cells (up, down, left, right) around
     * the position.
     * 
     * @param pos The center position
     * @return The lowest distance found, or -1 if no valid parent exists
     */
    private int findLowestNeighbor(GridPoint2 pos) {
        int minDistance = Integer.MAX_VALUE;
        boolean foundValidParent = false;

        for (int[] offset : NEIGHBOR_OFFSETS) {
            GridPoint2 neighborPos = new GridPoint2(pos.x + offset[0], pos.y + offset[1]);

            if (neighborPos.equals(colonyPosition)) {
                minDistance = 0;
                foundValidParent = true;
            } else {
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
        propagateRemoval(pos, targetDistance);
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
