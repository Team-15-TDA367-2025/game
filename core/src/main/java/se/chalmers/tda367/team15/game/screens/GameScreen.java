package se.chalmers.tda367.team15.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import se.chalmers.tda367.team15.game.controller.CameraController;
import se.chalmers.tda367.team15.game.controller.InputManager;
import se.chalmers.tda367.team15.game.controller.ViewportListener;
import se.chalmers.tda367.team15.game.model.GameModel;
import se.chalmers.tda367.team15.game.model.camera.CameraConstraints;
import se.chalmers.tda367.team15.game.model.camera.CameraModel;
import se.chalmers.tda367.team15.game.view.CameraView;
import se.chalmers.tda367.team15.game.view.GridView;
import se.chalmers.tda367.team15.game.view.HUDView;
import se.chalmers.tda367.team15.game.view.SceneView;
import se.chalmers.tda367.team15.game.view.TextureRegistry;

public class GameScreen extends ScreenAdapter {
    // World bounds - adjust these to match your game world size
    private static final float WORLD_SIZE = 200f;
    private static final float WORLD_VIEWPORT_WIDTH = 30f;

    private static final float MIN_ZOOM = 0.15f;
    private static final float MAX_ZOOM = 4.0f;

    // MVC components
    private final GameModel gameModel;
    private final CameraModel cameraModel;
    private final CameraView worldCameraView;
    private final OrthographicCamera hudCamera;
    private final CameraController cameraController;
    private final ViewportListener viewportListener;
    private final InputManager inputManager;

    // Views
    private final SceneView sceneView;
    private final GridView gridView;
    private final HUDView hudView;
    private final TextureRegistry textureRegistry;

    public GameScreen() {
        // Initialize world bounds and constraints
        Rectangle worldBounds = new Rectangle(-WORLD_SIZE / 2f, -WORLD_SIZE / 2f, WORLD_SIZE, WORLD_SIZE);
        CameraConstraints constraints = new CameraConstraints(worldBounds, MIN_ZOOM, MAX_ZOOM);

        // Initialize model
        cameraModel = new CameraModel(constraints);
        gameModel = new GameModel();
        gameModel.spawnAnt(new Vector2(0, 0));
        gameModel.spawnAnt(new Vector2(0, 0));
        gameModel.spawnAnt(new Vector2(0, 0));
        gameModel.spawnAnt(new Vector2(0, 0));
        gameModel.spawnAnt(new Vector2(0, 0));

        // Initialize cameras
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float aspectRatio = screenHeight / screenWidth;

        // World camera
        worldCameraView = new CameraView(cameraModel, WORLD_VIEWPORT_WIDTH, WORLD_VIEWPORT_WIDTH * aspectRatio);
        cameraController = new CameraController(cameraModel, worldCameraView);
        
        // HUD camera
        hudCamera = new OrthographicCamera(screenWidth, screenHeight);
        hudCamera.setToOrtho(false, screenWidth, screenHeight);
        
        // Input System
        inputManager = new InputManager();
        inputManager.addProcessor(cameraController);

        // Setup Rendering System
        textureRegistry = new TextureRegistry();
        sceneView = new SceneView(worldCameraView, textureRegistry);
        gridView = new GridView(worldCameraView, 5f);
        hudView = new HUDView(cameraModel, worldCameraView, hudCamera);

        // Setup Viewport Listener (Now simple and concrete)
        viewportListener = new ViewportListener(worldCameraView, hudCamera, hudView, WORLD_VIEWPORT_WIDTH);
    }

    @Override
    public void render(float delta) {
        // Update
        cameraController.update(delta);
        worldCameraView.updateCamera();
        gameModel.update(delta);

        // Render
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // World Render
        sceneView.render(gameModel.getDrawables());

        // Debug Grid
        gridView.render();

        // HUD Render
        hudView.render();
    }

    @Override
    public void resize(int width, int height) {
        viewportListener.resize(width, height);
    }

    @Override
    public void dispose() {
        sceneView.dispose();
        gridView.dispose();
        hudView.dispose();
        textureRegistry.dispose();
    }
}
