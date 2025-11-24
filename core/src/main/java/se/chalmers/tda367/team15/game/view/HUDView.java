package se.chalmers.tda367.team15.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import se.chalmers.tda367.team15.game.model.camera.CameraModel;

public class HUDView implements ViewportObserver {
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final CameraModel cameraModel;
    private final CameraView cameraView;
    private final OrthographicCamera hudCamera;

    public HUDView(CameraModel cameraModel, CameraView cameraView, OrthographicCamera hudCamera) {
        this.cameraModel = cameraModel;
        this.cameraView = cameraView;
        this.hudCamera = hudCamera;
        this.batch = new SpriteBatch();
        this.batch.setProjectionMatrix(hudCamera.combined);
        this.font = new BitmapFont();
    }

    public void render() {
        batch.begin();
        
        font.setColor(Color.WHITE);
        float y = Gdx.graphics.getHeight();
        float lineHeight = 25f;

        // camera debug info, just for testing, we will remove this later! (Debug)
        y -= 10f;
        font.draw(batch, String.format("FPS: %d", Gdx.graphics.getFramesPerSecond()), 10, y);
        y -= lineHeight;
        font.draw(batch, String.format("Camera xy: (%.1f, %.1f)",
                cameraModel.getPosition().x, cameraModel.getPosition().y), 10, y);
        y -= lineHeight;

        font.draw(batch, String.format("Zoom: %.2f", cameraModel.getZoom()), 10, y);
        y -= lineHeight;

        font.draw(batch, String.format("Viewport: %.1f x %.1f",
                cameraView.getViewportSize().x, cameraView.getViewportSize().y), 10, y);
        y -= lineHeight;
        // end of debug info
        
        batch.end();
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }

    @Override
    public void onViewportResize(int width, int height) {
        hudCamera.setToOrtho(false, width, height);
        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);
    }
}

