package se.chalmers.tda367.team15.game.model.managers;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.interfaces.providers.PheromoneUsageProvider;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGrid;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;

public class PheromoneManager implements PheromoneUsageProvider {
    private static final int[][] NEIGHBOR_OFFSETS = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };

    private final PheromoneGrid pheromoneGrid;
    private final GridPoint2 colonyPheromoneGridPosition;
    private final PheromoneGridConverter converter;
    private final int colonyGridSize;

    public PheromoneManager(GridPoint2 colonyWorldPosition, PheromoneGridConverter converter, int colonySizeInTiles) {
        this.pheromoneGrid = new PheromoneGrid();
        this.converter = converter;
        // Convert colony world position to pheromone grid coordinates
        Vector2 colonyWorldVec = new Vector2(colonyWorldPosition.x, colonyWorldPosition.y);
        this.colonyPheromoneGridPosition = converter.worldToPheromoneGrid(colonyWorldVec);
        this.colonyGridSize = colonySizeInTiles * converter.getPheromonesPerTile();
    }

    public PheromoneGridConverter getConverter() {
        return converter;
    }

    /**
     * Adds a pheromone at the specified position if it's valid.
     * A position is valid if it's adjacent to the colony or an existing pheromone
     * of the same type.
     * Multiple pheromone types can coexist at the same position.
     * 
     * @param pos  The position to add the pheromone
     * @param type The type of pheromone
     * @return true if the pheromone was added, false if the position was invalid
     */
    public boolean addPheromone(GridPoint2 pos, PheromoneType type) {
        // Check if this specific type already exists at this position
        if (pheromoneGrid.hasPheromoneAt(pos, type)) {
            return false;
        }

        int minDistance = findLowestNeighbor(pos, type);
        if (minDistance == -1) {
            // No valid parent found (not adjacent to colony or any pheromone of same type)
            return false;
        }

        Pheromone pheromone = new Pheromone(pos, type, minDistance + 1);
        pheromoneGrid.addPheromone(pheromone);

        // Update connected pheromones of same type that now have a shorter path
        propagateShorterDistances(pos, pheromone.getDistance(), type);

        return true;
    }

    /**
     * Propagates shorter distances to all reachable pheromones of the same type
     * using BFS.
     * 
     * @param startPos      The position from which to start propagation
     * @param startDistance The distance at the starting position
     * @param type          The pheromone type to propagate within
     */
    private void propagateShorterDistances(GridPoint2 startPos, int startDistance, PheromoneType type) {
        Deque<GridPoint2> queue = new ArrayDeque<>();
        queue.add(startPos);

        while (!queue.isEmpty()) {
            GridPoint2 pos = queue.poll();
            Pheromone current = pheromoneGrid.getPheromoneAt(pos, type);
            if (current == null) {
                continue;
            }
            int currentDistance = current.getDistance();

            for (int[] offset : NEIGHBOR_OFFSETS) {
                GridPoint2 neighborPos = new GridPoint2(pos.x + offset[0], pos.y + offset[1]);
                Pheromone neighbor = pheromoneGrid.getPheromoneAt(neighborPos, type);

                if (neighbor != null && neighbor.getDistance() > currentDistance + 1) {
                    neighbor.setDistance(currentDistance + 1);
                    queue.add(neighborPos);
                }
            }
        }
    }

    /**
     * Removes all pheromones of the specified type downstream of the given position
     * using BFS.
     * 
     * @param startPos    The position from which to start removal
     * @param minDistance Pheromones with distance > minDistance will be removed
     * @param type        The pheromone type to remove
     */
    private void propagateRemoval(GridPoint2 startPos, int minDistance, PheromoneType type) {
        Deque<GridPoint2> queue = new ArrayDeque<>();
        queue.add(startPos);

        while (!queue.isEmpty()) {
            GridPoint2 pos = queue.poll();

            for (int[] offset : NEIGHBOR_OFFSETS) {
                GridPoint2 neighborPos = new GridPoint2(pos.x + offset[0], pos.y + offset[1]);
                Pheromone neighbor = pheromoneGrid.getPheromoneAt(neighborPos, type);

                if (neighbor != null && neighbor.getDistance() > minDistance) {
                    pheromoneGrid.removePheromone(neighborPos, type);
                    queue.add(neighborPos);
                }
            }
        }
    }

    /**
     * Finds the lowest distance in adjacent cells for a specific pheromone type.
     * 
     * @param pos  The center position
     * @param type The pheromone type to search for
     * @return The lowest distance found, or -1 if no valid parent exists
     */
    private int findLowestNeighbor(GridPoint2 pos, PheromoneType type) {
        int minDistance = Integer.MAX_VALUE;
        boolean foundValidParent = false;

        for (int[] offset : NEIGHBOR_OFFSETS) {
            GridPoint2 neighborPos = new GridPoint2(pos.x + offset[0], pos.y + offset[1]);

            if (isInsideColony(neighborPos)) {
                // Calculate Manhattan distance from colony center
                int distanceFromCenter = Math.abs(neighborPos.x - colonyPheromoneGridPosition.x)
                        + Math.abs(neighborPos.y - colonyPheromoneGridPosition.y);
                if (distanceFromCenter < minDistance) {
                    minDistance = distanceFromCenter;
                    foundValidParent = true;
                }
            } else {
                // Only consider pheromones of the same type
                Pheromone pheromone = pheromoneGrid.getPheromoneAt(neighborPos, type);

                if (pheromone != null && pheromone.getDistance() < minDistance) {
                    minDistance = pheromone.getDistance();
                    foundValidParent = true;
                }
            }
        }

        return foundValidParent ? minDistance : -1;
    }

    private boolean isInsideColony(GridPoint2 pos) {
        return pos.dst(colonyPheromoneGridPosition) < colonyGridSize / 2;
    }

    /**
     * Removes the pheromone of the specified type at the position and all
     * subsequent
     * pheromones of the same type with strictly higher distance.
     * 
     * @param pos  The position where deletion starts
     * @param type The pheromone type to remove
     */
    public void removePheromone(GridPoint2 pos, PheromoneType type) {
        Pheromone pheromone = pheromoneGrid.getPheromoneAt(pos, type);
        if (pheromone == null) {
            return;
        }
        int targetDistance = pheromone.getDistance();
        pheromoneGrid.removePheromone(pos, type);
        propagateRemoval(pos, targetDistance, type);
    }

    /**
     * Removes all pheromones at the specified position (all types) and cascades
     * to remove downstream pheromones for each type.
     * 
     * @param pos The position where deletion starts
     */
    public void removeAllPheromones(GridPoint2 pos) {
        Collection<Pheromone> pheromones = pheromoneGrid.getPheromonesAt(pos);
        for (Pheromone pheromone : pheromones) {
            int targetDistance = pheromone.getDistance();
            PheromoneType type = pheromone.getType();
            pheromoneGrid.removePheromone(pos, type);
            propagateRemoval(pos, targetDistance, type);
        }
    }

    public Collection<Pheromone> getPheromones() {
        return pheromoneGrid.getAllPheromones();
    }

    /**
     * Gets a pheromone of a specific type at the position.
     */
    public Pheromone getPheromoneAt(GridPoint2 gridPos, PheromoneType type) {
        return pheromoneGrid.getPheromoneAt(gridPos, type);
    }

    /**
     * Gets all pheromones at the position (all types).
     */
    public Collection<Pheromone> getPheromonesAt(GridPoint2 gridPos) {
        return pheromoneGrid.getPheromonesAt(gridPos);
    }

    public List<Pheromone> getPheromonesIn3x3(GridPoint2 centerGridPos) {
        return pheromoneGrid.getPheromonesIn3x3(centerGridPos);
    }

    /** Fills all lines using strictly adjacent (non-diagonal) steps. */
    public GridPoint2 drawPheromonesBetween(GridPoint2 start, GridPoint2 end, PheromoneType type) {
        if (start == null) {
            processPheromoneAction(end, type);
            return end;
        }

        GridPoint2 current = start;
        while (!current.equals(end)) {
            int dx = end.x - current.x;
            int dy = end.y - current.y;

            GridPoint2 next;
            if (Math.abs(dx) > Math.abs(dy)) {
                next = new GridPoint2(current.x + Integer.signum(dx), current.y);
            } else {
                next = new GridPoint2(current.x, current.y + Integer.signum(dy));
            }

            if (!processPheromoneAction(next, type)) {
                return current;
            }
            current = next;
        }
        return current;
    }

    private boolean processPheromoneAction(GridPoint2 pos, PheromoneType type) {
        return addPheromone(pos, type) ||
                getPheromoneAt(pos, type) != null;
    }
}
