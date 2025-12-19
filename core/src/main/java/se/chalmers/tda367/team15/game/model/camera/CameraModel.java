package se.chalmers.tda367.team15.game.model.camera;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class CameraModel {
    private final Vector2 position = new Vector2(0, 0);
    private float zoom = 0.2f;
    private final CameraConstraints constraints;

    public CameraModel(CameraConstraints constraints) {
        this.constraints = constraints;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getZoom() {
        return zoom;
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
     * Zooms to a new level while keeping a specific world point visually fixed.
     * This updates both the zoom level and the camera position to create a "zoom
     * towards" effect.
     *
     * @param newZoom        The new zoom level (magnification)
     * @param invariantPoint The point in world coordinates that should remain fixed
     *                       (e.g., mouse cursor)
     */
    public void zoomTo(float newZoom, Vector2 invariantPoint) {
        float oldZoom = this.zoom;
        float clampedZoom = MathUtils.clamp(newZoom, constraints.getMinZoom(), constraints.getMaxZoom());

        if (clampedZoom == oldZoom)
            return;

        // Formula: C_new = P - (P - C_old) * (Zoom_old / Zoom_new)
        float scaleChange = oldZoom / clampedZoom;

        // Vector from Camera Center to Invariant Point
        Vector2 relativePoint = invariantPoint.cpy().sub(position);

        // Scale that vector
        relativePoint.scl(scaleChange);

        // New Center is Invariant Point minus the scaled vector
        position.set(invariantPoint.cpy().sub(relativePoint));

        this.zoom = clampedZoom;
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

}
