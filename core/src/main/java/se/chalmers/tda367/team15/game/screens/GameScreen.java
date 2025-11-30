package se.chalmers.tda367.team15.game.screens;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import se.chalmers.tda367.team15.game.controller.CameraController;
import se.chalmers.tda367.team15.game.controller.InputManager;
import se.chalmers.tda367.team15.game.controller.PheromoneController;
import se.chalmers.tda367.team15.game.model.GameModel;
import se.chalmers.tda367.team15.game.model.TimeCycle;
import se.chalmers.tda367.team15.game.model.camera.CameraConstraints;
import se.chalmers.tda367.team15.game.model.camera.CameraModel;
import se.chalmers.tda367.team15.game.view.CameraView;
import se.chalmers.tda367.team15.game.view.GridView;
import se.chalmers.tda367.team15.game.view.HUDView;
import se.chalmers.tda367.team15.game.view.PheromoneView;
import se.chalmers.tda367.team15.game.view.SceneView;
import se.chalmers.tda367.team15.game.view.TextureRegistry;
import se.chalmers.tda367.team15.game.view.ViewportListener;

public class GameScreen extends ScreenAdapter {
    private static final float WORLD_VIEWPORT_WIDTH = 30f;
    private static final int MAP_WIDTH = 200;
    private static final int MAP_HEIGHT = 200;
    private static final float TILE_SIZE = 1f;

    private static final float MIN_ZOOM = 0.15f;
    private static final float MAX_ZOOM = 4.0f;

    // MVC components
    private final GameModel gameModel;
    private final CameraModel cameraModel;
    private final CameraView worldCameraView;
    private final CameraController cameraController;
    private final PheromoneController pheromoneController;
    private final ViewportListener viewportListener;
    private final InputManager inputManager;

    // Views
    private final SceneView sceneView;
    private final GridView gridView;
    private final PheromoneView pheromoneView;
    private final HUDView hudView;
    private final TextureRegistry textureRegistry;

    public GameScreen() {
        // Initialize world bounds and constraints
        Rectangle worldBounds = new Rectangle(-MAP_WIDTH / 2f, -MAP_HEIGHT / 2f, MAP_WIDTH, MAP_HEIGHT);
        CameraConstraints constraints = new CameraConstraints(worldBounds, MIN_ZOOM, MAX_ZOOM);
        TimeCycle timeCycle = new TimeCycle(60);

        cameraModel = new CameraModel(constraints);
        gameModel = new GameModel(timeCycle, MAP_WIDTH, MAP_HEIGHT, TILE_SIZE);

        // TODO: Should be a factory or something, this is just for testing!
        gameModel.spawnAnt(new Vector2(0, 0));
        gameModel.spawnAnt(new Vector2(0, 0));
        gameModel.spawnAnt(new Vector2(0, 0));
        gameModel.spawnAnt(new Vector2(0, 0));
        gameModel.spawnAnt(new Vector2(0, 0));

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float aspectRatio = screenHeight / screenWidth;

        worldCameraView = new CameraView(cameraModel, WORLD_VIEWPORT_WIDTH, WORLD_VIEWPORT_WIDTH * aspectRatio);
        cameraController = new CameraController(cameraModel, worldCameraView);
        
        inputManager = new InputManager();
        inputManager.addProcessor(cameraController);

        textureRegistry = new TextureRegistry();
        sceneView = new SceneView(worldCameraView, textureRegistry);
        gridView = new GridView(worldCameraView, TILE_SIZE);
        hudView = new HUDView(cameraModel, worldCameraView);
        
        pheromoneController = new PheromoneController(gameModel, worldCameraView);
        hudView.setPheromoneController(pheromoneController);
        hudView.setPheromoneSystem(gameModel.getPheromoneSystem());

        // Add Stage first so it can handle button clicks before other processors
        inputManager.addProcessor(hudView.getStage());
        inputManager.addProcessor(pheromoneController);

        pheromoneView = new PheromoneView(worldCameraView, gameModel.getPheromoneSystem());

        viewportListener = new ViewportListener();
        viewportListener.addObserver(worldCameraView);
        viewportListener.addObserver(hudView);
    }

    @Override
    public void render(float delta) {
        // Update
        cameraController.update(delta);
        worldCameraView.updateCamera();
        gameModel.update(delta);

        ScreenUtils.clear(0.227f, 0.643f, 0.239f, 1f);

        pheromoneView.render();
        sceneView.render(gameModel.getDrawables(), gameModel.getFog());
        // gridView.render();
        hudView.render();
    }

    @Override
    public void resize(int width, int height) {
        viewportListener.resize(width, height);
        sceneView.resize(width, height);
    }

    @Override
    public void dispose() {
        sceneView.dispose();
        gridView.dispose();
        pheromoneView.dispose();
        hudView.dispose();
        textureRegistry.dispose();
    }
}
