package se.chalmers.tda367.team15.game.model.camera;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CameraModel {
    private Vector2 position = new Vector2(0, 0);
    private float zoom = 1f;
    private CameraConstraints constraints;

    public CameraModel(CameraConstraints constraints) {
        this.constraints = constraints;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getZoom() {
        return zoom;
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, zoom, zoom);
    }

    public void moveTo(Vector2 newPosition) {
        position.set(newPosition);
    }

    public void moveBy(Vector2 delta) {
        position.add(delta);
    }

    public void zoomTo(float newZoom) {
        this.zoom = MathUtils.clamp(newZoom, constraints.getMinZoom(), constraints.getMaxZoom());
    }

    /**
     * Applies constraints to the camera position considering the viewport size.
     * Ensures that the camera viewport corners stay within bounds when possible.
     * 
     * @param viewportSize The viewport size in world units (before zoom)
     */
    public void applyConstraints(Vector2 viewportSize) {
        position.set(constraints.constrainPosition(position, zoom, viewportSize));
    }

    /**
     * Converts a screen delta (pixel movement) to world delta (world unit
     * movement).
     * 
     * @param screenDelta  Screen delta in pixels (typically from mouse movement)
     * @param screenSize   Screen size in pixels
     * @param viewportSize Viewport size in world units (before zoom)
     * @return World delta in world units
     */
    public Vector2 screenDeltaToWorldDelta(Vector2 screenDelta, Vector2 screenSize, Vector2 viewportSize) {
        // Calculate effective viewport size (what we actually see in world coordinates)
        Vector2 effectiveViewportSize = viewportSize.cpy().scl(1f / zoom);
        // Convert screen delta to world delta
        Vector2 worldDelta = screenDelta.cpy();
        worldDelta.scl(effectiveViewportSize.x / screenSize.x,
                effectiveViewportSize.y / screenSize.y);
        return worldDelta;
    }

    /**
     * Converts screen coordinates to world coordinates.
     * 
     * @param screenPos    Screen position in pixels
     * @param screenSize   Screen size in pixels
     * @param viewportSize Viewport size in world units (before zoom)
     * @return World coordinates
     */
    public Vector2 screenToWorld(Vector2 screenPos, Vector2 screenSize, Vector2 viewportSize) {
        // Normalize screen position to [-1, 1] range (centered at origin)
        float normalizedX = (screenPos.x / screenSize.x) * 2f - 1f;
        float normalizedY = 1f - (screenPos.y / screenSize.y) * 2f; // Flip Y axis

        // Calculate effective viewport size (what we actually see in world coordinates)
        Vector2 effectiveViewportSize = viewportSize.cpy().scl(1f / zoom);

        // Convert normalized coordinates to world coordinates
        float worldX = position.x + normalizedX * effectiveViewportSize.x * 0.5f;
        float worldY = position.y + normalizedY * effectiveViewportSize.y * 0.5f;

        return new Vector2(worldX, worldY);
    }

    /**
     * Zooms around a specific screen point, keeping that point fixed in world
     * space.
     * 
     * @param screenPos    Screen position in pixels (the point to zoom around)
     * @param scrollAmount Scroll amount (positive = zoom in, negative = zoom out)
     * @param zoomSpeed    Zoom speed multiplier
     * @param screenSize   Screen size in pixels
     * @param viewportSize Viewport size in world units (before zoom)
     */
    public void zoomAround(Vector2 screenPos, float scrollAmount, float zoomSpeed,
            Vector2 screenSize, Vector2 viewportSize) {
        Vector2 worldPosBeforeZoom = screenToWorld(screenPos, screenSize, viewportSize);

        float zoomMultiplier = 1f + (-scrollAmount * zoomSpeed);
        float newZoom = zoom * zoomMultiplier;
        zoomTo(newZoom);

        Vector2 worldPosAfterZoom = screenToWorld(screenPos, screenSize, viewportSize);

        Vector2 offset = worldPosBeforeZoom.cpy().sub(worldPosAfterZoom);
        moveBy(offset);
    }
}
