package se.chalmers.tda367.team15.game.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.interfaces.PheromoneUsageProvider;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;

public class PheromoneController extends InputAdapter {
    private final PheromoneUsageProvider pheromoneUsageProvider;
    private final CoordinateConverter converter;
    private PheromoneType currentType = PheromoneType.GATHER; // null = delete mode
    private GridPoint2 lastGridPos; // Track last drawn position for line interpolation
    private boolean isDragging = false;

    public PheromoneController(PheromoneUsageProvider pheromoneUsageProvider, CoordinateConverter converter) {
        this.pheromoneUsageProvider = pheromoneUsageProvider;
        this.converter = converter;
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
            pheromoneUsageProvider.removeAllPheromones(gridPos);
            return;
        }

        lastGridPos = pheromoneUsageProvider.drawPheromonesBetween(lastGridPos, gridPos, currentType);
    }


    /**
     * Converts world coordinates to pheromone grid coordinates.
     * Uses the denser pheromone grid (multiple cells per tile).
     */
    private GridPoint2 worldToGrid(Vector2 worldPos) {
        return pheromoneUsageProvider.getConverter().worldToPheromoneGrid(worldPos);
    }
}
