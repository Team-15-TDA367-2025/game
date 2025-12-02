package se.chalmers.tda367.team15.game.view;

import java.util.LinkedHashSet;

/**
 * Listener to the viewport that notifies observers when the viewport is resized.
 */
public class ViewportListener {
    // We want to keep the order of the observers
    private final LinkedHashSet<ViewportObserver> observers;

    public ViewportListener() {
        this.observers = new LinkedHashSet<>();
    }

    public void addObserver(ViewportObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ViewportObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all registered observers of a viewport resize event.
     * 
     * @param width  The new width of the viewport
     * @param height The new height of the viewport
     */
    public void resize(int width, int height) {
        for (ViewportObserver observer : observers) {
            observer.onViewportResize(width, height);
        }
    }
}
