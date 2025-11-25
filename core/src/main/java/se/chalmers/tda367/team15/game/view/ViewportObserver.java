package se.chalmers.tda367.team15.game.view;

public interface ViewportObserver {
    /**
     * Called when the viewport is resized.
     * @param width The new width of the viewport
     * @param height The new height of the viewport
     */
    void onViewportResize(int width, int height);
}

