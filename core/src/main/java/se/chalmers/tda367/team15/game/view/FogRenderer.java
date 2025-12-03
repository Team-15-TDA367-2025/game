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

        batch.setColor(0.09f, 0.188f, 0.11f, 0.7f);
        float offsetX = -size.x / 2f;
        float offsetY = -size.y / 2f;

        for (int x = 0; x < size.x; x++) {
            for (int y = 0; y < size.y; y++) {
                if (!fogOfWar.isDiscovered(new GridPoint2(x, y))) {
                    float worldX = x + offsetX;
                    float worldY = y + offsetY;
                    batch.draw(pixelTexture, worldX, worldY, 1, 1);
                }
            }
        }

        batch.setColor(1, 1, 1, 1);
    }
}
