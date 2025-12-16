package se.chalmers.tda367.team15.game.screens.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;

import se.chalmers.tda367.team15.game.controller.*;
import se.chalmers.tda367.team15.game.model.AntFactory;
import se.chalmers.tda367.team15.game.model.DestructionListener;
import se.chalmers.tda367.team15.game.model.EnemyFactory;
import se.chalmers.tda367.team15.game.model.GameModel;
import se.chalmers.tda367.team15.game.model.GameWorld;
import se.chalmers.tda367.team15.game.model.TimeCycle;
import se.chalmers.tda367.team15.game.model.camera.CameraConstraints;
import se.chalmers.tda367.team15.game.model.camera.CameraModel;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;
import se.chalmers.tda367.team15.game.model.egg.EggManager;
import se.chalmers.tda367.team15.game.model.fog.FogSystem;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.managers.EntityManager;
import se.chalmers.tda367.team15.game.model.managers.SimulationManager;
import se.chalmers.tda367.team15.game.model.managers.StructureManager;
import se.chalmers.tda367.team15.game.model.managers.WaveManager;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.structure.Colony;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceSystem;
import se.chalmers.tda367.team15.game.model.world.TerrainFactory;
import se.chalmers.tda367.team15.game.model.world.TerrainGenerator;
import se.chalmers.tda367.team15.game.model.world.WorldMap;
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
        // 1. Create Models
        CameraModel cameraModel = createCameraModel();
        GameModel gameModel = createGameModel();

        // 2. Create Resources
        TextureRegistry textureRegistry = new TextureRegistry();
        UiFactory uiFactory = new UiFactory(textureRegistry);
        SpriteBatch hudBatch = new SpriteBatch();

        // 3. Create Views
        CameraView cameraView = createCameraView(cameraModel);
        WorldRenderer sceneView = new WorldRenderer(cameraView, textureRegistry, gameModel, gameModel.getFogProvider());
        PheromoneRenderer pheromoneView = new PheromoneRenderer(cameraView, gameModel.getPheromoneSystem());
        HudView hudView = new HudView(hudBatch, uiFactory);

        // 4. Create Controllers
        InputManager inputManager = new InputManager(); // Used for wiring but not stored in screen
        CameraController cameraController = new CameraController(cameraModel, cameraView);
        PheromoneController pheromoneController = new PheromoneController(gameModel, cameraView);
        SpeedController speedController = new SpeedController(gameModel);
        HudController hudController = new HudController(hudView, gameModel, pheromoneController, speedController,
                uiFactory, gameModel.getTimeCycle(), gameModel.getColonyUsageProvider());

        WaveManager waveManager = new WaveManager(gameModel);
        gameModel.getTimeCycle().addTimeObserver(waveManager);

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
        AntTypeRegistry antTypeRegistry = createAntTypeRegistry();

        TimeCycle timeCycle = new TimeCycle(TICKS_PER_MINUTE);
        TerrainGenerator terrainGenerator = TerrainFactory.createStandardPerlinGenerator(
                System.currentTimeMillis());
        // TODO: break this down
        SimulationManager simulationManager = new SimulationManager(timeCycle);

        DestructionListener destructionListener = new DestructionListener();
        EntityManager entityManager = new EntityManager();
        simulationManager.addUpdateObserver(entityManager);
        destructionListener.addEntityDeathObserver(entityManager);

        StructureManager structureManager = new StructureManager();
        simulationManager.addUpdateObserver(structureManager);
        destructionListener.addStructureDeathObserver(structureManager);

        ResourceSystem resourceSystem = new ResourceSystem(entityManager);
        simulationManager.addUpdateObserver(resourceSystem);

        GameWorld gameWorld = new GameWorld(MAP_WIDTH, MAP_HEIGHT, terrainGenerator, entityManager,
                structureManager, resourceSystem);
        // TODO: why is this needed?
        destructionListener.addStructureDeathObserver(gameWorld);

        WorldMap worldMap = new WorldMap(MAP_WIDTH, MAP_HEIGHT, terrainGenerator);

        EnemyFactory enemyFactory = new EnemyFactory(gameWorld, destructionListener);
        FogSystem fogSystem = new FogSystem(entityManager, worldMap);
        simulationManager.addUpdateObserver(fogSystem);
        PheromoneGridConverter pheromoneGridConverter = new PheromoneGridConverter(4);

        PheromoneSystem pheromoneSystem = new PheromoneSystem(new GridPoint2(0, 0), pheromoneGridConverter, 4);
        AntFactory antFactory = new AntFactory(pheromoneSystem, worldMap, entityManager,
                destructionListener);

        EggManager eggManager = new EggManager(antTypeRegistry, antFactory);
        timeCycle.addTimeObserver(eggManager);

        Colony colony = createColony(timeCycle, entityManager, eggManager,
                structureManager, destructionListener);

        spawnInitialAnts(entityManager, colony, antFactory, antTypeRegistry);

        return new GameModel(simulationManager, timeCycle, gameWorld, fogSystem, entityManager, colony, enemyFactory,
                pheromoneSystem, worldMap, antTypeRegistry);
    }

    public static void spawnInitialAnts(EntityManager entityManager, Home home, AntFactory antFactory,
            AntTypeRegistry antTypeRegistry) {
        AntType type = antTypeRegistry.get("worker");
        Ant ant = antFactory.createAnt(home, type);
        entityManager.addEntity(ant);
    }

    private static Colony createColony(TimeCycle timeCycle,
            EntityManager entityManager, EggManager eggManager, StructureManager structureManager,
            DestructionListener destructionListener) {
        Colony colony = new Colony(new GridPoint2(0, 0), timeCycle, entityManager, eggManager,
                entityManager, destructionListener);
        structureManager.addStructure(colony);
        eggManager.addObserver(colony);
        return colony;
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
    private static AntTypeRegistry createAntTypeRegistry() {
        AntTypeRegistry registry = new AntTypeRegistry();
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

        return registry;
    }
}
