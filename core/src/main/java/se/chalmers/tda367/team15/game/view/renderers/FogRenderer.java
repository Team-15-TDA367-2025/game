package se.chalmers.tda367.team15.game.view.renderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.fog.FogProvider;
import se.chalmers.tda367.team15.game.view.camera.CameraView;

public class FogRenderer {
    private final TextureRegion pixelTexture;
    private final FogProvider fogProvider;

    public FogRenderer(TextureRegion pixelTexture, FogProvider fogProvider) {
        this.pixelTexture = pixelTexture;
        this.fogProvider = fogProvider;
    }

    public void render(SpriteBatch batch, CameraView cameraView) {
        GridPoint2 size = fogProvider.getSize();

        batch.setColor(0.09f, 0.188f, 0.11f, 1f);
        float offsetX = -size.x / 2f;
        float offsetY = -size.y / 2f;

        Vector2 cameraPos = cameraView.getPosition();
        Vector2 viewportSize = cameraView.getEffectiveViewportSize();

        Vector2 halfViewport = new Vector2(viewportSize).scl(0.5f);
        // Add padding to ensure we cover the edges
        Vector2 leftBottom = new Vector2(cameraPos).sub(halfViewport).sub(1, 1);
        Vector2 rightTop = new Vector2(cameraPos).add(halfViewport).add(1, 1);

        // Calculate tile bounds relative to the map center offset
        // worldPos = tilePos + offset
        // tilePos = worldPos - offset
        int startX = Math.max(0, (int) Math.floor(leftBottom.x - offsetX));
        int startY = Math.max(0, (int) Math.floor(leftBottom.y - offsetY));
        int endX = Math.min(size.x, (int) Math.ceil(rightTop.x - offsetX));
        int endY = Math.min(size.y, (int) Math.ceil(rightTop.y - offsetY));

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                if (!fogProvider.isDiscovered(new GridPoint2(x, y))) {
                    float worldX = x + offsetX;
                    float worldY = y + offsetY;
                    batch.draw(pixelTexture, worldX, worldY, 1, 1);
                }
            }
        }

        batch.setColor(1, 1, 1, 1);
    }
}
