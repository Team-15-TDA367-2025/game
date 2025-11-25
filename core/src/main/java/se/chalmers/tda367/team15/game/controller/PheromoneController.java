package se.chalmers.tda367.team15.game.controller;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.GameModel;
import se.chalmers.tda367.team15.game.model.PheromoneType;

public class PheromoneController extends InputAdapter {
    private final GameModel gameModel;
    private final CoordinateConverter converter;
    private PheromoneType currentType;
    private boolean deleteMode;
    private GridPoint2 lastGridPos; // Track last drawn position for line interpolation

    public PheromoneController(GameModel gameModel, CoordinateConverter converter) {
        this.gameModel = gameModel;
        this.converter = converter;
        this.currentType = PheromoneType.GATHER;
        this.deleteMode = false;
        this.lastGridPos = null;
    }

    public void setCurrentType(PheromoneType type) {
        this.currentType = type;
        this.deleteMode = false;
    }

    public void setDeleteMode(boolean deleteMode) {
        this.deleteMode = deleteMode;
        if (deleteMode) {
            this.currentType = null;
        }
    }

    public PheromoneType getCurrentType() {
        return currentType;
    }

    public boolean isDeleteMode() {
        return deleteMode;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button != com.badlogic.gdx.Input.Buttons.LEFT) {
            return false;
        }
        // Reset last position for new stroke
        lastGridPos = null;
        // Don't consume the event - let Stage handle buttons first
        // Only process if we're in a valid mode
        if (deleteMode || currentType != null) {
            handleInput(screenX, screenY);
        }
        return false; // Return false so Stage can also process the event
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // Only process drag if we're in a valid mode
        if (deleteMode || currentType != null) {
            handleInput(screenX, screenY);
        }
        return false; // Return false so Stage can also process the event
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // Reset last position when touch ends
        lastGridPos = null;
        return false;
    }

    private void handleInput(int screenX, int screenY) {
        Vector2 screenPos = new Vector2(screenX, screenY);
        Vector2 worldPos = converter.screenToWorld(screenPos);
        
        // Convert world coordinates to grid coordinates (GridPoint2)
        GridPoint2 gridPos = worldToGrid(worldPos);

        if (deleteMode) {
            if (lastGridPos != null && !gridPos.equals(lastGridPos)) {
                // Fill in gaps when deleting
                lastGridPos = fillLine(lastGridPos, gridPos, true);
            } else {
                gameModel.getPheromoneSystem().removePheromone(gridPos);
                lastGridPos = gridPos;
            }
        } else if (currentType != null) {
            if (lastGridPos != null && !gridPos.equals(lastGridPos)) {
                // Fill in gaps when drawing to ensure continuous trail
                lastGridPos = fillLine(lastGridPos, gridPos, false);
            } else {
                // First point or same position
                boolean success = gameModel.getPheromoneSystem().addPheromone(gridPos, currentType);
                // Only update lastGridPos if we successfully added a pheromone
                if (success) {
                    lastGridPos = gridPos;
                }
            }
        }
    }

    /**
     * Fills in all grid cells along a line between two points using a path-finding approach
     * that only uses strictly adjacent (non-diagonal) steps, since pheromones must be adjacent.
     * This ensures continuous drawing even when touchDragged events skip cells during fast movement.
     * Skips the start point since it's already been drawn.
     * Returns the last successfully processed position.
     */
    private GridPoint2 fillLine(GridPoint2 start, GridPoint2 end, boolean isDelete) {
        // Handle edge case where start and end are the same
        if (start.equals(end)) {
            return start;
        }

        // Use a simple path-finding approach that only moves in strictly adjacent directions
        // This ensures each step is valid for pheromone placement
        GridPoint2 current = start;
        GridPoint2 lastSuccessful = start;
        
        // Move step by step towards the end, only using adjacent moves
        while (!current.equals(end)) {
            int dx = end.x - current.x;
            int dy = end.y - current.y;
            
            // Determine the next step (prefer the direction with larger distance)
            GridPoint2 next = null;
            if (Math.abs(dx) > Math.abs(dy)) {
                // Move horizontally
                next = new GridPoint2(current.x + (dx > 0 ? 1 : -1), current.y);
            } else if (dy != 0) {
                // Move vertically
                next = new GridPoint2(current.x, current.y + (dy > 0 ? 1 : -1));
            } else {
                // Already at end (shouldn't happen due to while condition)
                break;
            }
            
            // Try to process this cell
            if (isDelete) {
                gameModel.getPheromoneSystem().removePheromone(next);
                lastSuccessful = next;
            } else {
                boolean success = gameModel.getPheromoneSystem().addPheromone(next, currentType);
                if (success) {
                    lastSuccessful = next;
                } else {
                    // If it already exists, that's fine - update lastSuccessful and continue
                    if (gameModel.getPheromoneSystem().getPheromoneAt(next) != null) {
                        lastSuccessful = next;
                    } else {
                        // Can't add this cell (not adjacent to trail), stop here
                        break;
                    }
                }
            }
            
            current = next;
        }
        
        // Make sure we process the end point
        if (!end.equals(lastSuccessful)) {
            if (isDelete) {
                gameModel.getPheromoneSystem().removePheromone(end);
                lastSuccessful = end;
            } else {
                boolean success = gameModel.getPheromoneSystem().addPheromone(end, currentType);
                if (success || gameModel.getPheromoneSystem().getPheromoneAt(end) != null) {
                    lastSuccessful = end;
                }
            }
        }
        
        return lastSuccessful;
    }

    /**
     * Converts world coordinates to grid coordinates.
     * Uses 1:1 mapping (1 world unit = 1 grid cell).
     */
    private GridPoint2 worldToGrid(Vector2 worldPos) {
        return new GridPoint2((int) Math.floor(worldPos.x), (int) Math.floor(worldPos.y));
    }
}

