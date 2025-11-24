package se.chalmers.tda367.team15.game.view;

import java.util.ArrayList;
import java.util.List;

/**
 * Listener that notifies observers when the viewport is resized.
 */
public class ViewportListener {
    private final List<ViewportObserver> observers;

    public ViewportListener() {
        this.observers = new ArrayList<>();
    }

    public void addObserver(ViewportObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(ViewportObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all registered observers of a viewport resize event.
     * @param width The new width of the viewport
     * @param height The new height of the viewport
     */
    public void resize(int width, int height) {
        for (ViewportObserver observer : observers) {
            observer.onViewportResize(width, height);
        }
    }
}

