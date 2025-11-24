package se.chalmers.tda367.team15.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import se.chalmers.tda367.team15.game.model.CameraModel;
import se.chalmers.tda367.team15.game.view.CameraView;

public class CameraController extends InputAdapter {
    private CameraModel cameraModel;
    private CameraView cameraView;
    private float zoomSpeed = 0.1f;

    public CameraController(CameraModel model, CameraView cameraView) {
        this.cameraModel = model;
        this.cameraView = cameraView;
    }

    public void update() {
        handlePanInput();
    }

    private void handlePanInput() {
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            Vector2 mouseDelta = new Vector2(Gdx.input.getDeltaX(), -Gdx.input.getDeltaY());
            Vector2 screenSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Vector2 viewportSize = cameraView.getViewportSize();
            
            Vector2 worldDelta = cameraModel.screenDeltaToWorldDelta(mouseDelta, screenSize, viewportSize);
            cameraModel.moveBy(worldDelta.scl(-1));
            cameraModel.applyConstraints(viewportSize);
        }
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        Vector2 screenPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector2 screenSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Vector2 viewportSize = cameraView.getViewportSize();
        
        cameraModel.zoomAround(screenPos, amountY, zoomSpeed, screenSize, viewportSize);
        cameraModel.applyConstraints(viewportSize);
        cameraView.updateCamera();
        
        return true;
    }

    public void setZoomSpeed(float speed) {
        this.zoomSpeed = speed;
    }
}
