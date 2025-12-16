package se.chalmers.tda367.team15.game.controller;

import com.badlogic.gdx.math.Vector2;

/**
 * Interface for converting screen coordinates to world coordinates and getting
 * viewport info.
 * Implemented by the View, used by the Controller.
 */
public interface CoordinateConverter {
    /**
     * Converts screen coordinates to world coordinates.
     * 
     * @param screenPos Screen position in pixels
     * @return World coordinates
     */
    Vector2 screenToWorld(Vector2 screenPos);

    /**
     * Converts a screen delta (pixel movement) to world delta (world unit
     * movement).
     * 
     * @param screenDelta Screen delta in pixels
     * @param screenSize  Total screen size in pixels
     * @return World delta in world units
     */
    Vector2 screenDeltaToWorldDelta(Vector2 screenDelta, Vector2 screenSize);

    /**
     * Gets the viewport size in world units (before zoom).
     * 
     * @return Viewport size
     */
    Vector2 getViewportSize();
}
