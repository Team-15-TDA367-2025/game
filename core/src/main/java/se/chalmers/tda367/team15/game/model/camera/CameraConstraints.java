package se.chalmers.tda367.team15.game.model.camera;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CameraConstraints {
    private final float minZoom;
    private final float maxZoom;

    // You can never move the camera so that any part is outside of this rectangle.
    private final Rectangle bounds;

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

    /**
     * Constrains a camera position considering the viewport size and zoom.
     * Ensures that the camera viewport corners stay within bounds when possible.
     *
     * @param position     The current camera position
     * @param zoom         The current zoom level
     * @param viewportSize The viewport size in world units (before zoom)
     * @return A new Vector2 with the constrained position
     */
    public Vector2 constrainPosition(Vector2 position, float zoom, Vector2 viewportSize) {
        Vector2 scaledViewport = viewportSize.cpy().scl(1 / zoom);

        float availableWidth = Math.max(0, bounds.width - scaledViewport.x);
        float availableHeight = Math.max(0, bounds.height - scaledViewport.y);

        Rectangle movementArea = new Rectangle(
                bounds.x + (bounds.width - availableWidth) / 2f,
                bounds.y + (bounds.height - availableHeight) / 2f,
                availableWidth,
                availableHeight);

        return new Vector2(
                MathUtils.clamp(position.x, movementArea.x, movementArea.x + movementArea.width),
                MathUtils.clamp(position.y, movementArea.y, movementArea.y + movementArea.height));
    }
}
