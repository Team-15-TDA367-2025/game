package se.chalmers.tda367.team15.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import se.chalmers.tda367.team15.game.controller.CameraController;
import se.chalmers.tda367.team15.game.controller.HudController;
import se.chalmers.tda367.team15.game.controller.ViewportListener;
import se.chalmers.tda367.team15.game.model.CameraConstraints;
import se.chalmers.tda367.team15.game.model.CameraModel;
import se.chalmers.tda367.team15.game.view.CameraView;
import se.chalmers.tda367.team15.game.view.HudView;
import se.chalmers.tda367.team15.game.view.SceneView;

public class Main extends ApplicationAdapter {
    // World bounds - adjust these to match your game world size
    private static final float WORLD_SIZE = 200f;
    private static final float WORLD_VIEWPORT_WIDTH = 30f;

    private static final float MIN_ZOOM = 0.15f;
    private static final float MAX_ZOOM = 4.0f;

    // MVC components
    private CameraModel cameraModel;
    private CameraView worldCameraView;
    private OrthographicCamera hudCamera;
    private CameraController cameraController;
    private ViewportListener viewportListener;
    private SceneView sceneView;
    private HudView hudView;
    private HudController hudController;

    // Rendering
    private SpriteBatch hudBatch;
    private Texture image;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        // Initialize world bounds and constraints
        Rectangle worldBounds = new Rectangle(-WORLD_SIZE / 2f, -WORLD_SIZE / 2f, WORLD_SIZE, WORLD_SIZE);
        CameraConstraints constraints = new CameraConstraints(worldBounds, MIN_ZOOM, MAX_ZOOM);

        // Initialize model
        cameraModel = new CameraModel(constraints);

        // Initialize cameras
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float aspectRatio = screenHeight / screenWidth;

        // World camera
        worldCameraView = new CameraView(cameraModel, WORLD_VIEWPORT_WIDTH, WORLD_VIEWPORT_WIDTH * aspectRatio);

        cameraController = new CameraController(cameraModel, worldCameraView);
        Gdx.input.setInputProcessor(cameraController);

        // HUD camera
        hudCamera = new OrthographicCamera(screenWidth, screenHeight);
        hudCamera.setToOrtho(false, screenWidth, screenHeight);

        // Setup viewport listener with resize handlers
        viewportListener = new ViewportListener();
        // World camera: maintain aspect ratio with fixed viewport width
        viewportListener.addResizeHandler((width, height) -> {
            float worldAspectRatio = (float) height / (float) width;
            worldCameraView.setViewport(WORLD_VIEWPORT_WIDTH, WORLD_VIEWPORT_WIDTH * worldAspectRatio);
        });

        // HUD camera: match screen dimensions exactly
        viewportListener.addResizeHandler((width, height) -> {
            hudCamera.setToOrtho(false, width, height);
            hudCamera.update();
            // Update HUD batch projection when camera changes
            hudBatch.setProjectionMatrix(hudCamera.combined);
        });

        sceneView = new SceneView(worldCameraView);
        hudBatch = new SpriteBatch();
        hudBatch.setProjectionMatrix(hudCamera.combined);
        image = new Texture(Gdx.files.internal("libgdx.png"));
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();

        hudView = new HudView(hudBatch);
        hudController = new HudController(hudView);

        InputMultiplexer mux = new InputMultiplexer();
        mux.addProcessor(hudView.getTopStage());
        mux.addProcessor(hudView.getBottomStage());
        mux.addProcessor(cameraController);
        Gdx.input.setInputProcessor(mux);
    }

    @Override
    public void render() {
        cameraController.update();
        worldCameraView.updateCamera();

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Update shape renderer projection (world camera may have changed)
        shapeRenderer.setProjectionMatrix(worldCameraView.getCombinedMatrix());
        drawGrid();

        sceneView.render(() -> {
            // Render world entities here
            sceneView.getBatch().draw(image, 0, 0, 12f, 2f);
        });

        hudController.update(Gdx.graphics.getDeltaTime());
        hudView.render(Gdx.graphics.getDeltaTime());

        hudBatch.begin();
        drawHUD();
        hudBatch.end();
    }

    // AI Generated for testing. Remove later.
    private void drawGrid() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GRAY);

        float gridSize = 5f; // Grid cell size in world units
        Vector2 effectiveViewportSize = worldCameraView.getViewportSize().scl(worldCameraView.getCamera().zoom);
        float startX = (float) Math
                .floor((worldCameraView.getCamera().position.x - effectiveViewportSize.x / 2f) / gridSize) * gridSize;
        float endX = (float) Math
                .ceil((worldCameraView.getCamera().position.x + effectiveViewportSize.x / 2f) / gridSize) * gridSize;
        float startY = (float) Math
                .floor((worldCameraView.getCamera().position.y - effectiveViewportSize.y / 2f) / gridSize) * gridSize;
        float endY = (float) Math
                .ceil((worldCameraView.getCamera().position.y + effectiveViewportSize.y / 2f) / gridSize) * gridSize;

        // Draw vertical lines
        for (float x = startX; x <= endX; x += gridSize) {
            shapeRenderer.line(x, startY, x, endY);
        }

        // Draw horizontal lines
        for (float y = startY; y <= endY; y += gridSize) {
            shapeRenderer.line(startX, y, endX, y);
        }

        shapeRenderer.end();
    }

    private void drawHUD() {
        font.setColor(Color.WHITE);
        float y = Gdx.graphics.getHeight();
        float lineHeight = 25f;

        // camera debug info
        y -= 10f;
        font.draw(hudBatch, String.format("FPS: %d", Gdx.graphics.getFramesPerSecond()), 10, y);
        y -= lineHeight;
        font.draw(hudBatch, String.format("Camera xy: (%.1f, %.1f)",
                cameraModel.getPosition().x, cameraModel.getPosition().y), 10, y);
        y -= lineHeight;

        font.draw(hudBatch, String.format("Zoom: %.2f", cameraModel.getZoom()), 10, y);
        y -= lineHeight;

        font.draw(hudBatch, String.format("Viewport: %.1f x %.1f",
                worldCameraView.getViewportSize().x, worldCameraView.getViewportSize().y), 10, y);
        y -= lineHeight;
        // end of debug info
    }

    @Override
    public void resize(int width, int height) {
        viewportListener.resize(width, height);
        hudView.resize(width, height);
    }

    @Override
    public void dispose() {
        sceneView.dispose();
        hudBatch.dispose();
        image.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
