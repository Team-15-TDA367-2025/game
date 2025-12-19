package se.chalmers.tda367.team15.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.camera.CameraModel;

public class CameraController extends InputAdapter {
    private final CameraModel cameraModel;
    private final CoordinateConverter converter;
    private float zoomSpeed = 0.1f;
    private final float moveSpeed = 20f; // World units per second

    public CameraController(CameraModel model, CoordinateConverter converter) {
        this.cameraModel = model;
        this.converter = converter;
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
            cameraModel.applyConstraints(converter.getViewportSize());
        }
    }

    private void handlePanInput() {
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            Vector2 mouseDelta = new Vector2(Gdx.input.getDeltaX(), -Gdx.input.getDeltaY());
            Vector2 screenSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            Vector2 worldDelta = converter.screenDeltaToWorldDelta(mouseDelta, screenSize);
            cameraModel.moveBy(worldDelta.scl(-1));
            cameraModel.applyConstraints(converter.getViewportSize());
        }
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        Vector2 screenPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());

        // Get current world position of mouse
        Vector2 worldPos = converter.screenToWorld(screenPos);

        // Calculate new zoom
        float zoomMultiplier = 1f + (-amountY * zoomSpeed);
        float newZoom = cameraModel.getZoom() * zoomMultiplier;

        // Apply zoom while keeping mouse position fixed in world
        cameraModel.zoomTo(newZoom, worldPos);

        // Apply constraints
        cameraModel.applyConstraints(converter.getViewportSize());

        return true;
    }

    public void setZoomSpeed(float speed) {
        this.zoomSpeed = speed;
    }
}
