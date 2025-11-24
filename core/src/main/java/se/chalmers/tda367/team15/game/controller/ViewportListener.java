package se.chalmers.tda367.team15.game.controller;

import java.util.ArrayList;
import java.util.List;

public class ViewportListener {
    @FunctionalInterface
    public interface ResizeHandler {
        void onResize(int width, int height);
    }

    private List<ResizeHandler> resizeHandlers = new ArrayList<>();

    public ViewportListener() {
    }

    public void addResizeHandler(ResizeHandler handler) {
        resizeHandlers.add(handler);
    }

    public void removeResizeHandler(ResizeHandler handler) {
        resizeHandlers.remove(handler);
    }

    public void resize(int width, int height) {
        for (ResizeHandler handler : resizeHandlers) {
            handler.onResize(width, height);
        }
    }
}
