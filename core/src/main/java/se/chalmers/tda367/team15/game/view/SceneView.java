package se.chalmers.tda367.team15.game.view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.FogOfWar;
import se.chalmers.tda367.team15.game.model.GameModel;

public class SceneView {
    private final SpriteBatch batch;
    private final CameraView cameraView;
    private final TextureRegistry textureRegistry;
    private final TerrainRenderer terrainRenderer;
    private final FogRenderer fogRenderer;
    private final GameModel model;

    public SceneView(CameraView cameraView, TextureRegistry textureRegistry, GameModel model) {
        this.cameraView = cameraView;
        this.textureRegistry = textureRegistry;
        this.batch = new SpriteBatch();
        this.terrainRenderer = new TerrainRenderer(textureRegistry);
        this.fogRenderer = new FogRenderer(textureRegistry.get("pixel"));
        this.model = model;
    }

    public void render(Iterable<Drawable> drawables, FogOfWar fog) {
        batch.setProjectionMatrix(cameraView.getCombinedMatrix());
        batch.begin();

        terrainRenderer.render(batch, model.getWorldMap(), cameraView);
        drawables.forEach(this::draw);
        fogRenderer.render(batch, fog);

        batch.end();

    }

    private void draw(Drawable drawable) {
        TextureRegion region = textureRegistry.get(drawable.getTextureName());

        // With TILE_SIZE = 1, world units ARE tile units, no conversion needed
        float width = drawable.getSize().x;
        float height = drawable.getSize().y;

        // Center origin for rotation
        float originX = width / 2f;
        float originY = height / 2f;

        // Position is the center of the object
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
