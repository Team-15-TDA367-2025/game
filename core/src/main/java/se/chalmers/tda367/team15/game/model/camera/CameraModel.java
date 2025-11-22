package se.chalmers.tda367.team15.game.model.camera;

import com.badlogic.gdx.math.MathUtils;
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

}
