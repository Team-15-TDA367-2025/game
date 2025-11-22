package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.Rectangle;

public class CameraConstraints {
    private float minZoom;
    private float maxZoom;

    // You can never move the camera so that any part is outside of this rectangle.
    private Rectangle bounds;

    public CameraConstraints(Rectangle bounds, float minZoom, float maxZoom) {
        if (minZoom <= 0) {
            throw new IllegalArgumentException("Min zoom must be greater than 0");
        }
        if (maxZoom <= minZoom) {
            throw new IllegalArgumentException("Max zoom must be greater than min zoom");
        }

        this.bounds = bounds;
        this.minZoom = minZoom;
        this.maxZoom = maxZoom;
    }

    public float getMinZoom() {
        return minZoom;
    }

    public float getMaxZoom() {
        return maxZoom;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isInBounds(Rectangle rect) {
        return bounds.contains(rect);
    }
}
