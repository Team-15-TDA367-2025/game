package se.chalmers.tda367.team15.game.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;

// AI Generated for testing, should not look like this later!
public class PheromoneView {
    private final ShapeRenderer shapeRenderer;
    private final CameraView cameraView;
    private final PheromoneSystem pheromoneSystem;

    public PheromoneView(CameraView cameraView, PheromoneSystem pheromoneSystem) {
        this.cameraView = cameraView;
        this.pheromoneSystem = pheromoneSystem;
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
            
            // Calculate fade factor based on distance with a curve
            // This will be used to fade colors towards black
            float fadeFactor = calculateFadeFactor(pheromone.getDistance(), maxDistance);
            
            // Fade color towards black by multiplying RGB by fadeFactor
            // fadeFactor = 1.0 means full color, fadeFactor = 0.0 means black
            Color color = new Color(
                baseColor.r * fadeFactor,
                baseColor.g * fadeFactor,
                baseColor.b * fadeFactor,
                1.0f  // Keep alpha at 1.0, we're fading the color itself
            );
            shapeRenderer.setColor(color);
            
            GridPoint2 gridPos = pheromone.getPosition();
            Vector2 pos = new Vector2(gridPos.x, gridPos.y);
            // Draw a 1x1 square centered at the grid position
            shapeRenderer.rect(pos.x, pos.y, 1f, 1f);
        }

        shapeRenderer.end();
    }

    /**
     * Calculates fade factor based on distance using a curve that keeps colors visible at the start.
     * Closer pheromones (lower distance) have higher fade factor (brighter colors).
     * Colors fade towards black as distance increases.
     * @param distance The distance of the pheromone
     * @param maxDistance The maximum distance in the system (for normalization)
     * @return Fade factor between 0.2 and 1.0 (1.0 = full color, 0.2 = very dark but still visible)
     */
    private float calculateFadeFactor(int distance, int maxDistance) {
        if (maxDistance == 0 || distance == 0) {
            return 1.0f;
        }
        
        // Use a gentler curve that keeps early pheromones very visible
        // Using a power curve: fadeFactor = 1 - (normalizedDistance^power)
        // Lower power = more gradual fade, keeping more visible at start
        
        float normalizedDistance = (float) distance / Math.max(maxDistance, 1);
        
        // Use a power curve with power around 1.5-2.0 for gradual fade
        // This keeps the first 50% of the path very visible
        float power = 1.8f;
        float fadeFactor = 1.0f - (float) Math.pow(normalizedDistance, power);
        
        // Clamp between 0.2 and 1.0
        // 0.2 ensures even far pheromones are still somewhat visible (dark but not black)
        fadeFactor = Math.max(0.2f, Math.min(1.0f, fadeFactor));
        
        return fadeFactor;
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

