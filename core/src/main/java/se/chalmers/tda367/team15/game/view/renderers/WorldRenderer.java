package se.chalmers.tda367.team15.game.view.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;

import se.chalmers.tda367.team15.game.GameLaunchConfiguration;
import se.chalmers.tda367.team15.game.model.fog.FogProvider;
import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.interfaces.TimeCycleDataProvider;
import se.chalmers.tda367.team15.game.model.world.MapProvider;
import se.chalmers.tda367.team15.game.view.TextureRegistry;
import se.chalmers.tda367.team15.game.view.camera.CameraView;

public class WorldRenderer {
    private final SpriteBatch batch;
    private final CameraView cameraView;
    private final TextureRegistry textureRegistry;
    private final TerrainRenderer terrainRenderer;
    private final FogRenderer fogRenderer;
    private final MapProvider mapProvider;
    private final ShapeRenderer shapeRenderer;
    private final FogProvider fogProvider;
    private final TimeCycleDataProvider timeProvider;

    public WorldRenderer(CameraView cameraView, TextureRegistry textureRegistry, MapProvider mapProvider,
            TimeCycleDataProvider timeProvider, FogProvider fogProvider) {
        this.cameraView = cameraView;
        this.textureRegistry = textureRegistry;
        this.batch = new SpriteBatch();
        this.terrainRenderer = new TerrainRenderer(textureRegistry);
        this.fogProvider = fogProvider;
        this.fogRenderer = new FogRenderer(textureRegistry.get("pixel"));
        this.mapProvider = mapProvider;
        this.shapeRenderer = new ShapeRenderer();
        this.timeProvider = timeProvider;
    }

    public void render(Iterable<Drawable> drawables) {
        batch.setProjectionMatrix(cameraView.getCombinedMatrix());
        batch.begin();

        terrainRenderer.render(batch, mapProvider, cameraView);
        drawables.forEach(this::draw);

        batch.end();

        // Render fog after main batch to avoid z-fighting
        if (!GameLaunchConfiguration.getCurrent().noFog()) {
            fogRenderer.render(fogProvider, cameraView.getCombinedMatrix(), cameraView);
        }

        if (!timeProvider.getIsDay()) {
            // at night, we draw a black rectangle over screen 50% opacity
            GridPoint2 dimensions = mapProvider.getSize();
            Gdx.gl.glEnable(GL20.GL_BLEND); // we want to blend not cover.
            shapeRenderer.setProjectionMatrix(cameraView.getCombinedMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.65f);
            shapeRenderer.rect(-((float) dimensions.x / 2), -((float) dimensions.y / 2), dimensions.x, dimensions.y);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

    }

    private void draw(Drawable drawable) {
        TextureRegion region = textureRegistry.get(drawable.getTextureName());

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
                MathUtils.radiansToDegrees * (drawable.getRotation() - (MathUtils.PI / 2f)));
    }

    public void dispose() {
        batch.dispose();
        fogRenderer.dispose();
        shapeRenderer.dispose();
    }
}
