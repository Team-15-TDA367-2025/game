package se.chalmers.tda367.team15.game.view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import se.chalmers.tda367.team15.game.model.world.Tile;
import se.chalmers.tda367.team15.game.model.world.WorldMap;

public class TerrainRenderer {
    private final TextureRegistry textures;
    private final ObjectMap<String, TextureRegion> textureCache;

    public TerrainRenderer(TextureRegistry textures) {
        this.textures = textures;
        this.textureCache = new ObjectMap<>();
    }

    public void render(SpriteBatch batch, WorldMap worldMap, CameraView cameraView) {
        final float tileSize = worldMap.getTileSize();
        final GridPoint2 size = worldMap.getSize();
        final Vector2 offset = new Vector2(-size.x / 2f * tileSize, -size.y / 2f * tileSize);

        Vector2 cameraPos = cameraView.getPosition();
        Vector2 viewportSize = cameraView.getEffectiveViewportSize();
        
        Vector2 halfViewport = new Vector2(viewportSize).scl(0.5f);
        Vector2 leftBottom = new Vector2(cameraPos).sub(halfViewport).sub(tileSize, tileSize);
        Vector2 rightTop = new Vector2(cameraPos).add(halfViewport).add(tileSize, tileSize);

        GridPoint2 startTile = worldMap.worldToTile(leftBottom);
        GridPoint2 endTile = worldMap.worldToTile(rightTop);
        
        int startX = Math.max(0, startTile.x);
        int endX = Math.min(size.x, endTile.x + 1);
        int startY = Math.max(0, startTile.y);
        int endY = Math.min(size.y, endTile.y + 1);

        TextureRegion grass1 = getOrCacheTexture("grass1");
        TextureRegion grass2 = getOrCacheTexture("grass2");
        TextureRegion grass3 = getOrCacheTexture("grass3");

        // Batch by texture to minimize texture switches
        // Render all grass1 tiles, then grass2, then grass3
        renderTilesOfTexture(batch, worldMap, startX, endX, startY, endY, offset, tileSize, "grass1", grass1);
        renderTilesOfTexture(batch, worldMap, startX, endX, startY, endY, offset, tileSize, "grass2", grass2);
        renderTilesOfTexture(batch, worldMap, startX, endX, startY, endY, offset, tileSize, "grass3", grass3);
    }
    
    private void renderTilesOfTexture(SpriteBatch batch, WorldMap worldMap, int startX, int endX, int startY, int endY,
                                     Vector2 offset, float tileSize, String textureName, TextureRegion texture) {
        GridPoint2 tilePos = new GridPoint2();
        Vector2 worldPos = new Vector2();
        
        for (int y = startY; y < endY; y++) {
            tilePos.y = y;
            worldPos.y = y * tileSize + offset.y;
            for (int x = startX; x < endX; x++) {
                tilePos.x = x;
                Tile tile = worldMap.getTileUnchecked(tilePos);
                if (tile != null && textureName.equals(tile.getTextureName())) {
                    worldPos.x = x * tileSize + offset.x;
                    batch.draw(texture, worldPos.x, worldPos.y, tileSize, tileSize);
                }
            }
        }
    }

    private TextureRegion getOrCacheTexture(String name) {
        TextureRegion texture = textureCache.get(name);
        if (texture == null) {
            texture = textures.get(name);
            textureCache.put(name, texture);
        }
        return texture;
    }
}
