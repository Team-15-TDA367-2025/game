package se.chalmers.tda367.team15.game.controller;

import com.badlogic.gdx.graphics.OrthographicCamera;
import se.chalmers.tda367.team15.game.view.CameraView;
import se.chalmers.tda367.team15.game.view.HUDView;

public class ViewportListener {
    private final CameraView worldCamera;
    private final OrthographicCamera hudCamera;
    private final HUDView hudView;
    private final float worldViewportWidth;

    public ViewportListener(CameraView worldCamera, OrthographicCamera hudCamera, HUDView hudView, float worldViewportWidth) {
        this.worldCamera = worldCamera;
        this.hudCamera = hudCamera;
        this.hudView = hudView;
        this.worldViewportWidth = worldViewportWidth;
    }

    public void resize(int width, int height) {
        // 1. Update World Camera (Fixed Width, variable height)
        float aspectRatio = (float) height / (float) width;
        worldCamera.setViewport(worldViewportWidth, worldViewportWidth * aspectRatio);

        // 2. Update HUD Camera (Match screen exactly)
        hudCamera.setToOrtho(false, width, height);
        hudCamera.update();
        hudView.updateProjectionMatrix(hudCamera);
    }
}
