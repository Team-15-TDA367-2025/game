package se.chalmers.tda367.team15.game.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.GameModel;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;

public class PheromoneController extends InputAdapter {
    private final GameModel gameModel;
    private final CoordinateConverter converter;
    private final PheromoneGridConverter pheromoneGridConverter;
    private PheromoneType currentType = PheromoneType.GATHER; // null = delete mode
    private GridPoint2 lastGridPos; // Track last drawn position for line interpolation
    private boolean isDragging = false;

    public PheromoneController(GameModel gameModel, CoordinateConverter converter) {
        this.gameModel = gameModel;
        this.converter = converter;
        this.pheromoneGridConverter = gameModel.getPheromoneGridConverter();
        this.lastGridPos = null;
    }

    public void setCurrentType(PheromoneType type) {
        this.currentType = type;
    }

    public PheromoneType getCurrentType() {
        return currentType;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT) {
            return false;
        }
        isDragging = true;
        lastGridPos = null;

        handleInput(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!isDragging) {
            return false;
        }

        handleInput(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        lastGridPos = null;
        isDragging = false;
        return false;
    }

    private void handleInput(int screenX, int screenY) {
        Vector2 screenPos = new Vector2(screenX, screenY);
        Vector2 worldPos = converter.screenToWorld(screenPos);

        GridPoint2 gridPos = worldToGrid(worldPos);

        if (currentType == null) {
            gameModel.getPheromoneManager().removePheromone(gridPos);
            return;
        }

        lastGridPos = fillLine(lastGridPos, gridPos);
    }

    /** Fills all lines using strictly adjacent (non-diagonal) steps. */
    private GridPoint2 fillLine(GridPoint2 start, GridPoint2 end) {
        if (start == null) {
            processPheromoneAction(end);
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

            if (!processPheromoneAction(next)) {
                return current;
            }
            current = next;
        }
        return current;
    }

    private boolean processPheromoneAction(GridPoint2 pos) {
        return gameModel.getPheromoneManager().addPheromone(pos, currentType) ||
                gameModel.getPheromoneManager().getPheromoneAt(pos) != null;
    }

    /**
     * Converts world coordinates to pheromone grid coordinates.
     * Uses the denser pheromone grid (multiple cells per tile).
     */
    private GridPoint2 worldToGrid(Vector2 worldPos) {
        return pheromoneGridConverter.worldToPheromoneGrid(worldPos);
    }
}
