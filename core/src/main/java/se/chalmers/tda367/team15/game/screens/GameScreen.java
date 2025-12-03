package se.chalmers.tda367.team15.game.screens;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import se.chalmers.tda367.team15.game.controller.CameraController;
import se.chalmers.tda367.team15.game.controller.HudController;
import se.chalmers.tda367.team15.game.controller.InputManager;
import se.chalmers.tda367.team15.game.controller.PheromoneController;
import se.chalmers.tda367.team15.game.model.GameModel;
import se.chalmers.tda367.team15.game.model.TimeCycle;
import se.chalmers.tda367.team15.game.model.camera.CameraConstraints;
import se.chalmers.tda367.team15.game.model.camera.CameraModel;
import se.chalmers.tda367.team15.game.model.world.PerlinNoiseTerrainGenerator;
import se.chalmers.tda367.team15.game.model.world.TerrainGenerator;
import se.chalmers.tda367.team15.game.view.CameraView;
import se.chalmers.tda367.team15.game.view.HudView;
import se.chalmers.tda367.team15.game.view.PheromoneView;
import se.chalmers.tda367.team15.game.view.SceneView;
import se.chalmers.tda367.team15.game.view.TextureRegistry;
import se.chalmers.tda367.team15.game.view.ViewportListener;

public class GameScreen extends ScreenAdapter {
    private static final float WORLD_VIEWPORT_WIDTH = 15f; // Show ~15 tiles across
    private static final int MAP_WIDTH = 200;  // 200 tiles wide
    private static final int MAP_HEIGHT = 200; // 200 tiles tall

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
    private final HudController hudController;

    // Views
    private final SceneView sceneView;
    private final SpriteBatch hudBatch;
    private final PheromoneView pheromoneView;
    private final HudView hudView;
    private final TextureRegistry textureRegistry;

    public GameScreen() {
        // Initialize world bounds and constraints
        Rectangle worldBounds = new Rectangle(-MAP_WIDTH / 2f, -MAP_HEIGHT / 2f, MAP_WIDTH, MAP_HEIGHT);
        CameraConstraints constraints = new CameraConstraints(worldBounds, MIN_ZOOM, MAX_ZOOM);
        TimeCycle timeCycle = new TimeCycle(60);

        cameraModel = new CameraModel(constraints);
        
        TerrainGenerator terrainGenerator = new PerlinNoiseTerrainGenerator(
            List.of("grass1", "grass2", "grass3"),
            System.currentTimeMillis() // Random seed
        );
        gameModel = new GameModel(timeCycle, MAP_WIDTH, MAP_HEIGHT, terrainGenerator);

        // TODO: Should be a factory or something, this is just for testing!
        gameModel.spawnAnt(new Vector2(0, 0));
        gameModel.spawnAnt(new Vector2(0, 0));
        gameModel.spawnAnt(new Vector2(0, 0));
        gameModel.spawnAnt(new Vector2(0, 0));
        gameModel.spawnAnt(new Vector2(0, 0));

        gameModel.spawnTermite(new Vector2(10, 0));

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float aspectRatio = screenHeight / screenWidth;

        worldCameraView = new CameraView(cameraModel, WORLD_VIEWPORT_WIDTH, WORLD_VIEWPORT_WIDTH * aspectRatio);
        cameraController = new CameraController(cameraModel, worldCameraView);

        hudBatch = new SpriteBatch();
        inputManager = new InputManager();
        inputManager.addProcessor(cameraController);

        textureRegistry = new TextureRegistry();
        sceneView = new SceneView(worldCameraView, textureRegistry, gameModel);

        hudView = new HudView(hudBatch);
        pheromoneController = new PheromoneController(gameModel, worldCameraView);
        hudController = new HudController(hudView, gameModel, pheromoneController);

        // Add Stage first so it can handle button clicks before other processors
        inputManager.addProcessor(hudView.getTopStage());
        inputManager.addProcessor(hudView.getBottomStage());
        inputManager.addProcessor(pheromoneController);

        pheromoneView = new PheromoneView(worldCameraView, gameModel.getPheromoneSystem());

        viewportListener = new ViewportListener();
        viewportListener.addObserver(worldCameraView);
        // viewportListener.addObserver(hudView);

    }

    @Override
    public void render(float delta) {
        // Update
        cameraController.update(delta);
        worldCameraView.updateCamera();
        gameModel.update(delta);
        hudController.update(delta);

        ScreenUtils.clear(0.227f, 0.643f, 0.239f, 1f);

        sceneView.render(gameModel.getDrawables(), gameModel.getFog());
        pheromoneView.render();
        // gridView.render();
        //hudController.update(Gdx.graphics.getDeltaTime());
        hudView.render(Gdx.graphics.getDeltaTime());
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
        pheromoneView.dispose();
        hudView.dispose();
        textureRegistry.dispose();
    }
}
