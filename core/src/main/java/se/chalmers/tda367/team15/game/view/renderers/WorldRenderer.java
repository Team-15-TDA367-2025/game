package se.chalmers.tda367.team15.game.view.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;

import se.chalmers.tda367.team15.game.model.interfaces.GameObject;
import se.chalmers.tda367.team15.game.model.interfaces.providers.TimeCycleDataProvider;
import se.chalmers.tda367.team15.game.model.world.MapProvider;
import se.chalmers.tda367.team15.game.view.TextureResolver;
import se.chalmers.tda367.team15.game.view.camera.CameraView;
import se.chalmers.tda367.team15.game.view.camera.ViewportListener;

public class WorldRenderer {
    private final SpriteBatch batch;
    private final CameraView cameraView;
    private final TextureResolver textureResolver;
    private final TerrainRenderer terrainRenderer;
    private final FogRenderer fogRenderer;
    private final MapProvider mapProvider;
    private final ShapeRenderer shapeRenderer;
    private final TimeCycleDataProvider timeProvider;
    private final boolean disableFog;

    public WorldRenderer(CameraView cameraView, TextureResolver textureResolver, MapProvider mapProvider,
            TimeCycleDataProvider timeProvider, FogRenderer fogRenderer, ViewportListener viewportListener,
            boolean disableFog) {
        this.cameraView = cameraView;
        this.textureResolver = textureResolver;
        this.batch = new SpriteBatch();
        this.terrainRenderer = new TerrainRenderer(textureResolver);
        this.fogRenderer = fogRenderer;
        viewportListener.addObserver(fogRenderer);
        this.mapProvider = mapProvider;
        this.shapeRenderer = new ShapeRenderer();
        this.timeProvider = timeProvider;
        this.disableFog = disableFog;
    }

    public void render(Iterable<GameObject> drawables) {
        batch.setProjectionMatrix(cameraView.getCombinedMatrix());
        batch.begin();

        terrainRenderer.render(batch, mapProvider, cameraView);
        drawables.forEach(this::draw);

        batch.end();

        // Render fog after main batch to avoid z-fighting
        if (!disableFog) {
            fogRenderer.render(cameraView.getCombinedMatrix(), cameraView);
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

    private void draw(GameObject drawable) {
        TextureRegion region = textureResolver.resolve(drawable);

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
        fogRenderer.dispose();
        shapeRenderer.dispose();
    }
}
