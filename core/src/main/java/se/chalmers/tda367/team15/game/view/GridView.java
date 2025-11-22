package se.chalmers.tda367.team15.game.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

// Just for testing, we will remove this later! (Debug)
public class GridView {
    private final ShapeRenderer shapeRenderer;
    private final CameraView cameraView;
    private final float gridSize;

    public GridView(CameraView cameraView, float gridSize) {
        this.cameraView = cameraView;
        this.gridSize = gridSize;
        this.shapeRenderer = new ShapeRenderer();
    }

    public void render() {
        Matrix4 projectionMatrix = cameraView.getCombinedMatrix();
        shapeRenderer.setProjectionMatrix(projectionMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GRAY);

        Vector2 effectiveViewportSize = cameraView.getEffectiveViewportSize();
        Vector2 cameraPosition = cameraView.getCameraPosition();
        float startX = (float) Math
                .floor((cameraPosition.x - effectiveViewportSize.x / 2f) / gridSize) * gridSize;
        float endX = (float) Math
                .ceil((cameraPosition.x + effectiveViewportSize.x / 2f) / gridSize) * gridSize;
        float startY = (float) Math
                .floor((cameraPosition.y - effectiveViewportSize.y / 2f) / gridSize) * gridSize;
        float endY = (float) Math
                .ceil((cameraPosition.y + effectiveViewportSize.y / 2f) / gridSize) * gridSize;

        // Draw vertical lines
        for (float x = startX; x <= endX; x += gridSize) {
            shapeRenderer.line(x, startY, x, endY);
        }

        // Draw horizontal lines
        for (float y = startY; y <= endY; y += gridSize) {
            shapeRenderer.line(startX, y, endX, y);
        }

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}

