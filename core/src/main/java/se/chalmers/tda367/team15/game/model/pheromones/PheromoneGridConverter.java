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
    private final float pheromoneCellSize;

    public PheromoneGridConverter(int pheromonesPerTile) {
        this.pheromonesPerTile = pheromonesPerTile;
        this.pheromoneCellSize = 1f / pheromonesPerTile;
    }

    /**
     * Converts world coordinates to pheromone grid coordinates.
     * 
     * @param worldPos World position
     * @return Pheromone grid position
     */
    public GridPoint2 worldToPheromoneGrid(Vector2 worldPos) {
        return new GridPoint2(
                (int) Math.floor(worldPos.x / pheromoneCellSize),
                (int) Math.floor(worldPos.y / pheromoneCellSize));
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
                gridPos.x * pheromoneCellSize + pheromoneCellSize / 2f,
                gridPos.y * pheromoneCellSize + pheromoneCellSize / 2f);
    }

    /**
     * Gets the size of a pheromone cell in world units.
     * 
     * @return Pheromone cell size
     */
    public float getPheromoneCellSize() {
        return pheromoneCellSize;
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
