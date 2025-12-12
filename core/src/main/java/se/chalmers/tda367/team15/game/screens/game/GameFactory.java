package se.chalmers.tda367.team15.game.screens.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import se.chalmers.tda367.team15.game.controller.*;
import se.chalmers.tda367.team15.game.model.GameModel;
import se.chalmers.tda367.team15.game.model.GameWorld;
import se.chalmers.tda367.team15.game.model.SimulationHandler;
import se.chalmers.tda367.team15.game.model.TimeCycle;
import se.chalmers.tda367.team15.game.model.camera.CameraConstraints;
import se.chalmers.tda367.team15.game.model.camera.CameraModel;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;
import se.chalmers.tda367.team15.game.model.world.TerrainFactory;
import se.chalmers.tda367.team15.game.model.world.TerrainGenerator;
import se.chalmers.tda367.team15.game.view.TextureRegistry;
import se.chalmers.tda367.team15.game.view.camera.CameraView;
import se.chalmers.tda367.team15.game.view.camera.ViewportListener;
import se.chalmers.tda367.team15.game.view.renderers.PheromoneRenderer;
import se.chalmers.tda367.team15.game.view.renderers.WorldRenderer;
import se.chalmers.tda367.team15.game.view.ui.HudView;
import se.chalmers.tda367.team15.game.view.ui.UiFactory;

/**
 * Factory for creating and wiring the GameScreen.
 */
public class GameFactory {
    public static final int MAP_WIDTH = 400;
    public static final int MAP_HEIGHT = 400;
    public static final float WORLD_VIEWPORT_WIDTH = 15f;
    public static final float MIN_ZOOM = 0.05f;
    public static final float MAX_ZOOM = 4.0f;
    public static final int TICKS_PER_MINUTE = 6;

    private GameFactory() {
    }

    public static GameScreen createGameScreen(Game game) {
        // 0. Initialize ant types (must happen before models)
        initializeAntTypes();

        // 1. Create Models
        CameraModel cameraModel = createCameraModel();
        GameModel gameModel = createGameModel();

        gameModel.spawnInitialAnts();

        // 2. Create Resources
        TextureRegistry textureRegistry = new TextureRegistry();
        UiFactory uiFactory = new UiFactory(textureRegistry);
        SpriteBatch hudBatch = new SpriteBatch();

        // 3. Create Views
        CameraView cameraView = createCameraView(cameraModel);
        WorldRenderer sceneView = new WorldRenderer(cameraView, textureRegistry, gameModel);
        PheromoneRenderer pheromoneView = new PheromoneRenderer(cameraView, gameModel.getPheromoneSystem());
        HudView hudView = new HudView(hudBatch, uiFactory);

        // 4. Create Controllers
        InputManager inputManager = new InputManager(); // Used for wiring but not stored in screen
        CameraController cameraController = new CameraController(cameraModel, cameraView);
        PheromoneController pheromoneController = new PheromoneController(gameModel, cameraView);
        SpeedController speedController = new SpeedController(gameModel);
        HudController hudController = new HudController(hudView, gameModel, pheromoneController, speedController, uiFactory);

        // 5. Wire Input
        inputManager.addProcessor(cameraController);
        inputManager.addProcessor(hudView.getStage());
        inputManager.addProcessor(pheromoneController);

        // 6. Wire Listeners
        ViewportListener viewportListener = new ViewportListener();
        viewportListener.addObserver(cameraView);

        return new GameScreen(
                game,
                gameModel,
                cameraView,
                sceneView,
                pheromoneView,
                hudView,
                textureRegistry,
                uiFactory,
                viewportListener,
                cameraController,
                hudController);
    }

    private static CameraModel createCameraModel() {
        Rectangle worldBounds = new Rectangle(
                -MAP_WIDTH / 2f, -MAP_HEIGHT / 2f,
                MAP_WIDTH, MAP_HEIGHT);
        CameraConstraints constraints = new CameraConstraints(
                worldBounds, MIN_ZOOM, MAX_ZOOM);
        return new CameraModel(constraints);
    }

    private static GameModel createGameModel() {
        TimeCycle timeCycle = new TimeCycle(TICKS_PER_MINUTE);
        TerrainGenerator terrainGenerator = TerrainFactory.createStandardPerlinGenerator(
            System.currentTimeMillis()
        );
        SimulationHandler simulationHandler = new SimulationHandler(timeCycle);
        GameWorld gameWorld = new GameWorld(simulationHandler, MAP_WIDTH, MAP_HEIGHT, terrainGenerator);

        return new GameModel(timeCycle, simulationHandler, gameWorld);
    }

    private static CameraView createCameraView(CameraModel cameraModel) {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float aspectRatio = screenWidth == 0 ? 1f : screenHeight / screenWidth;

        return new CameraView(
                cameraModel,
                WORLD_VIEWPORT_WIDTH,
                WORLD_VIEWPORT_WIDTH * aspectRatio);
    }

    /**
     * Initializes and registers all ant types in the registry.
     * This must be called before creating GameModel to ensure ant types are
     * available.
     */
    private static void initializeAntTypes() {
        AntTypeRegistry registry = AntTypeRegistry.getInstance();
        registry.clear();

        // Scout: High speed, low HP, 0 capacity, cheap/fast to hatch
        registry.register(new AntType(
                "scout",
                "Scout",
                5, // Food Cost
                30, // 30 ticks (0.5 min)
                4f, // Max Health
                8f, // Speed
                0, // Capacity
                "scout" // Texture
        ));

        // Soldier: Low speed, high HP, 0 capacity, expensive
        registry.register(new AntType(
                "soldier",
                "Soldier",
                40, // Food Cost
                300, // 5 min
                20f, // Max Health
                2f, // Speed
                0, // Capacity
                "ant" // Texture
        ));

        // Worker: Medium speed, medium HP, some capacity
        registry.register(new AntType(
                "worker",
                "Worker",
                10, // Food Cost
                60, // 1 min
                6f, // Max Health
                5f, // Speed
                10, // Capacity
                "ant" // Texture
        ));
    }
}
