package se.chalmers.tda367.team15.game.view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SceneView {
    private SpriteBatch batch;
    private CameraView cameraView;

    public SceneView(CameraView cameraView) {
        this.cameraView = cameraView;
        this.batch = new SpriteBatch();
    }

    public void render(Runnable renderWorld) {
        batch.setProjectionMatrix(cameraView.getCombinedMatrix());

        batch.begin();
        renderWorld.run();
        batch.end();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
    }
}
