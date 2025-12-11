package se.chalmers.tda367.team15.game.view.renderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.world.Tile;
import se.chalmers.tda367.team15.game.model.world.WorldMap;
import se.chalmers.tda367.team15.game.view.TextureRegistry;
import se.chalmers.tda367.team15.game.view.camera.CameraView;

public class TerrainRenderer {
    private final TextureRegistry textureRegistry;

    public TerrainRenderer(TextureRegistry textureRegistry) {
        this.textureRegistry = textureRegistry;
    }

    public void render(SpriteBatch batch, WorldMap worldMap, CameraView cameraView) {
        final GridPoint2 size = worldMap.getSize();
        final float offsetX = -size.x / 2f;
        final float offsetY = -size.y / 2f;

        Vector2 cameraPos = cameraView.getPosition();
        Vector2 viewportSize = cameraView.getEffectiveViewportSize();

        Vector2 halfViewport = new Vector2(viewportSize).scl(0.5f);
        Vector2 leftBottom = new Vector2(cameraPos).sub(halfViewport).sub(1, 1);
        Vector2 rightTop = new Vector2(cameraPos).add(halfViewport).add(1, 1);

        GridPoint2 startTile = worldMap.worldToTile(leftBottom);
        GridPoint2 endTile = worldMap.worldToTile(rightTop);

        for (int y = startTile.y; y < endTile.y; y++) {
            for (int x = startTile.x; x < endTile.x; x++) {
                Tile tile = worldMap.getTile(new GridPoint2(x, y));

                if (tile != null) {
                    TextureRegion texture = textureRegistry.get(tile.getTextureName());
                    batch.draw(texture, x + offsetX, y + offsetY, 1, 1);
                }
            }
        }
    }
}
