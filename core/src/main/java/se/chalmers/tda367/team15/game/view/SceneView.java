package se.chalmers.tda367.team15.game.view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import se.chalmers.tda367.team15.game.model.interfaces.Drawable;

public class SceneView {
    private final SpriteBatch batch;
    private final CameraView cameraView;
    private final TextureRegistry textureRegistry;

    public SceneView(CameraView cameraView, TextureRegistry textureRegistry) {
        this.cameraView = cameraView;
        this.textureRegistry = textureRegistry;
        this.batch = new SpriteBatch();
    }

    public void render(Iterable<Drawable> drawables) {
        batch.setProjectionMatrix(cameraView.getCombinedMatrix());
        batch.begin();

        drawables.forEach(this::draw);

        batch.end();
    }

    private void draw(Drawable drawable) {
        TextureRegion region = textureRegistry.get(drawable.getTextureName());

        float width = drawable.getSize().x;
        float height = drawable.getSize().y;

        // Center origin for rotation
        float originX = width / 2f;
        float originY = height / 2f;

        // Position (assuming model position is center, adjust if it's bottom-left)
        // If model.getPosition() is the center of the object:
        float x = drawable.getPosition().x - originX;
        float y = drawable.getPosition().y - originY;

        batch.draw(region,
                x, y,
                originX, originY,
                width, height,
                1f, 1f,
                MathUtils.radiansToDegrees * drawable.getRotation());
    }

    public void dispose() {
        batch.dispose();
    }
}
