package se.chalmers.tda367.team15.game.view;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import se.chalmers.tda367.team15.game.model.FogOfWar;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FogRenderer {
    private final TextureRegion pixelTexture;

    public FogRenderer(TextureRegion pixelTexture) {
        this.pixelTexture = pixelTexture;
    }

    public void render(SpriteBatch batch, FogOfWar fogOfWar) {
        int width = fogOfWar.getWidth();
        int height = fogOfWar.getHeight();
        float tileSize = fogOfWar.getTileSize();

        batch.setColor(0.09f, 0.188f, 0.11f, 0.7f);
        float offsetX = -fogOfWar.getWidth() / 2f * fogOfWar.getTileSize(); // AI debugging for coordinates
        float offsetY = -fogOfWar.getHeight() / 2f * fogOfWar.getTileSize();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!fogOfWar.isDiscovered(x, y)) {
                    float worldX = x * tileSize + offsetX;
                    float worldY = y * tileSize + offsetY;
                    batch.draw(pixelTexture, worldX, worldY, tileSize, tileSize);
                }
            }
        }

        batch.setColor(1, 1, 1, 1);

    }
}
