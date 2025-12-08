package se.chalmers.tda367.team15.game.view.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;
import se.chalmers.tda367.team15.game.view.camera.CameraView;

// AI Generated for testing, should not look like this later!
public class PheromoneRenderer {
    private final ShapeRenderer shapeRenderer;
    private final CameraView cameraView;
    private final PheromoneSystem pheromoneSystem;
    private final PheromoneGridConverter converter;

    public PheromoneRenderer(CameraView cameraView, PheromoneSystem pheromoneSystem) {
        this.cameraView = cameraView;
        this.pheromoneSystem = pheromoneSystem;
        this.converter = pheromoneSystem.getConverter();
        this.shapeRenderer = new ShapeRenderer();
    }

    public void render() {
        Matrix4 projectionMatrix = cameraView.getCombinedMatrix();
        shapeRenderer.setProjectionMatrix(projectionMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Find max distance for normalization
        int maxDistance = 0;
        for (Pheromone pheromone : pheromoneSystem.getPheromones()) {
            maxDistance = Math.max(maxDistance, pheromone.getDistance());
        }

        for (Pheromone pheromone : pheromoneSystem.getPheromones()) {
            Color baseColor = getColorForType(pheromone.getType());
            float fadeFactor = calculateFadeFactor(pheromone.getDistance(), maxDistance);

            Color color = new Color(
                    baseColor.r * fadeFactor,
                    baseColor.g * fadeFactor,
                    baseColor.b * fadeFactor,
                    1.0f);
            shapeRenderer.setColor(color);

            GridPoint2 gridPos = pheromone.getPosition();
            Vector2 worldPos = converter.pheromoneGridToWorld(gridPos);
            float cellSize = converter.getPheromoneCellSize();
            // Draw a square at the pheromone cell size, centered at the world position
            shapeRenderer.rect(worldPos.x - cellSize / 2f, worldPos.y - cellSize / 2f, cellSize, cellSize);
        }

        shapeRenderer.end();
    }

    private float calculateFadeFactor(int distance, int maxDistance) {
        if (maxDistance == 0 || distance == 0) {
            return 1.0f;
        }

        float normalizedDistance = (float) distance / Math.max(maxDistance, 1);
        float power = 1.8f;
        float fadeFactor = 1.0f - (float) Math.pow(normalizedDistance, power);
        return Math.max(0.2f, Math.min(1.0f, fadeFactor));
    }

    private Color getColorForType(PheromoneType type) {
        switch (type) {
            case GATHER:
                return Color.BLUE;
            case ATTACK:
                return Color.RED;
            case EXPLORE:
                return Color.GREEN;
            default:
                return Color.WHITE;
        }
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
