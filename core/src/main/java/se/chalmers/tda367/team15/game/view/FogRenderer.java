package se.chalmers.tda367.team15.game.view;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.FogOfWar;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FogRenderer {
    private final TextureRegion pixelTexture;

    public FogRenderer(TextureRegion pixelTexture) {
        this.pixelTexture = pixelTexture;
    }

    public void render(SpriteBatch batch, FogOfWar fogOfWar) {
        GridPoint2 size = fogOfWar.getSize();
        float tileSize = fogOfWar.getTileSize();

        batch.setColor(0.09f, 0.188f, 0.11f, 0.7f);
        float offsetX = -size.x / 2f * fogOfWar.getTileSize(); // AI debugging for coordinates
        float offsetY = -size.y / 2f * fogOfWar.getTileSize();

        for (int x = 0; x < size.x; x++) {
            for (int y = 0; y < size.y; y++) {
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
