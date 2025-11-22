package se.chalmers.tda367.team15.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.camera.CameraModel;
import se.chalmers.tda367.team15.game.view.CameraView;

public class CameraController extends InputAdapter {
    private CameraModel cameraModel;
    private CameraView cameraView;
    private float zoomSpeed = 0.1f;
    private float moveSpeed = 20f; // World units per second

    public CameraController(CameraModel model, CameraView cameraView) {
        this.cameraModel = model;
        this.cameraView = cameraView;
    }

    public void update(float deltaTime) {
        handlePanInput();
        handleKeyboardInput(deltaTime);
    }

    private void handleKeyboardInput(float dt) {
        float moveAmount = moveSpeed * (1 / cameraModel.getZoom()) * dt;
        Vector2 delta = new Vector2();

        if (Gdx.input.isKeyPressed(Input.Keys.W))
            delta.y += moveAmount;
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            delta.y -= moveAmount;
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            delta.x -= moveAmount;
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            delta.x += moveAmount;

        if (delta.len2() > 0) {
            cameraModel.moveBy(delta);
            cameraModel.applyConstraints(cameraView.getViewportSize());
        }
    }

    private void handlePanInput() {
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            Vector2 mouseDelta = new Vector2(Gdx.input.getDeltaX(), -Gdx.input.getDeltaY());
            Vector2 screenSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            Vector2 worldDelta = cameraView.screenDeltaToWorldDelta(mouseDelta, screenSize);
            cameraModel.moveBy(worldDelta.scl(-1));
            cameraModel.applyConstraints(cameraView.getViewportSize());
        }
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        Vector2 screenPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        
        // 1. Get world position before zoom
        Vector2 worldPosBeforeZoom = cameraView.screenToWorld(screenPos);

        // 2. Calculate and set new zoom
        float zoomMultiplier = 1f + (-amountY * zoomSpeed);
        float newZoom = cameraModel.getZoom() * zoomMultiplier;
        cameraModel.zoomTo(newZoom);
        
        // 3. Update view to reflect new zoom (needed for accurate screenToWorld calculation)
        cameraView.updateCamera();

        // 4. Get world position after zoom (it will have shifted because camera center didn't change)
        Vector2 worldPosAfterZoom = cameraView.screenToWorld(screenPos);

        // 5. Move camera to compensate for the shift, keeping the mouse point fixed
        Vector2 offset = worldPosBeforeZoom.cpy().sub(worldPosAfterZoom);
        cameraModel.moveBy(offset);
        
        // 6. Apply constraints
        cameraModel.applyConstraints(cameraView.getViewportSize());

        return true;
    }

    public void setZoomSpeed(float speed) {
        this.zoomSpeed = speed;
    }
}
