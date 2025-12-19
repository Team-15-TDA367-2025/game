package se.chalmers.tda367.team15.game.model.pheromones;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

/**
 * Utility class for converting between world coordinates and pheromone grid
 * coordinates.
 * Pheromones use a denser grid than tiles - multiple pheromone cells per tile.
 */
public class PheromoneGridConverter {
    private final int pheromonesPerTile;

    public PheromoneGridConverter(int pheromonesPerTile) {
        this.pheromonesPerTile = pheromonesPerTile;
    }

    /**
     * Converts world coordinates to pheromone grid coordinates.
     * 
     * @param worldPos World position
     * @return Pheromone grid position
     */
    public GridPoint2 worldToPheromoneGrid(Vector2 worldPos) {
        return new GridPoint2(
                (int) Math.floor(worldPos.x / getPheromoneCellSize()),
                (int) Math.floor(worldPos.y / getPheromoneCellSize()));
    }

    /**
     * Converts pheromone grid coordinates to world coordinates (center of pheromone
     * cell).
     * 
     * @param gridPos Pheromone grid position
     * @return World position (center of the pheromone cell)
     */
    public Vector2 pheromoneGridToWorld(GridPoint2 gridPos) {
        return new Vector2(
                gridPos.x * getPheromoneCellSize() + getPheromoneCellSize() / 2f,
                gridPos.y * getPheromoneCellSize() + getPheromoneCellSize() / 2f);
    }

    /**
     * Gets the size of a pheromone cell in world units.
     * 
     * @return Pheromone cell size
     */
    public float getPheromoneCellSize() {
        return 1f / pheromonesPerTile;
    }

    /**
     * Gets the number of pheromones per tile.
     * 
     * @return Pheromones per tile
     */
    public int getPheromonesPerTile() {
        return pheromonesPerTile;
    }
}
