package se.chalmers.tda367.team15.game.screens.game;

import java.util.Set;

import java.util.HashMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.GameConfiguration;
import se.chalmers.tda367.team15.game.controller.CameraController;
import se.chalmers.tda367.team15.game.controller.HudController;
import se.chalmers.tda367.team15.game.controller.InputManager;
import se.chalmers.tda367.team15.game.controller.PheromoneController;
import se.chalmers.tda367.team15.game.controller.SpeedController;
import se.chalmers.tda367.team15.game.model.AntFactory;
import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.DestructionListener;
import se.chalmers.tda367.team15.game.model.EnemyFactory;
import se.chalmers.tda367.team15.game.model.GameModel;
import se.chalmers.tda367.team15.game.model.TimeCycle;
import se.chalmers.tda367.team15.game.model.camera.CameraConstraints;
import se.chalmers.tda367.team15.game.model.camera.CameraModel;
import se.chalmers.tda367.team15.game.model.egg.EggManager;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;
import se.chalmers.tda367.team15.game.model.fog.FogManager;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.managers.EntityManager;
import se.chalmers.tda367.team15.game.model.managers.PheromoneManager;
import se.chalmers.tda367.team15.game.model.managers.ResourceManager;
import se.chalmers.tda367.team15.game.model.managers.SimulationManager;
import se.chalmers.tda367.team15.game.model.managers.StructureManager;
import se.chalmers.tda367.team15.game.model.managers.WaveManager;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;
import se.chalmers.tda367.team15.game.model.structure.Colony;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceNodeFactory;
import se.chalmers.tda367.team15.game.model.world.MapProvider;
import se.chalmers.tda367.team15.game.model.world.TerrainFactory;
import se.chalmers.tda367.team15.game.model.world.TerrainGenerator;
import se.chalmers.tda367.team15.game.model.world.WorldMap;
import se.chalmers.tda367.team15.game.model.world.terrain.StructureSpawn;
import se.chalmers.tda367.team15.game.view.TextureRegistry;
import se.chalmers.tda367.team15.game.view.camera.CameraView;
import se.chalmers.tda367.team15.game.view.camera.ViewportListener;
import se.chalmers.tda367.team15.game.view.renderers.FogRenderer;
import se.chalmers.tda367.team15.game.view.renderers.PheromoneRenderer;
import se.chalmers.tda367.team15.game.view.renderers.WorldRenderer;
import se.chalmers.tda367.team15.game.view.ui.HudView;
import se.chalmers.tda367.team15.game.view.ui.UiSkin;

/**
 * Factory for creating and wiring the GameScreen.
 */
public class GameFactory {
    public final GameConfiguration gameConfiguration;

    public GameFactory(GameConfiguration gameConfiguration) {
        this.gameConfiguration = gameConfiguration;
    }

    public GameScreen createGameScreen(Game game) {

        GridPoint2 mapSize = gameConfiguration.mapSize();

        // 1. Create Models
        CameraModel cameraModel = createCameraModel(mapSize);
        GameModel gameModel = createGameModel(mapSize);
        ViewportListener viewportListener = new ViewportListener();

        // 2. Create Resources
        TextureRegistry textureRegistry = new TextureRegistry();
        UiSkin uiFactory = new UiSkin(textureRegistry);
        SpriteBatch hudBatch = new SpriteBatch();

        // 3. Create Views
        CameraView cameraView = createCameraView(cameraModel);
        FogRenderer fogRenderer = new FogRenderer(gameModel.getFogProvider());
        WorldRenderer worldRenderer = new WorldRenderer(cameraView, textureRegistry, gameModel.getMapProvider(),
                gameModel.getTimeProvider(), fogRenderer, viewportListener, gameConfiguration.noFog());
        PheromoneRenderer pheromoneView = new PheromoneRenderer(cameraView, gameModel.getPheromoneUsageProvider());
        HudView hudView = new HudView(hudBatch, uiFactory);

        // 4. Create Controllers
        InputManager inputManager = new InputManager(); // Used for wiring but not stored in screen
        CameraController cameraController = new CameraController(cameraModel, cameraView);
        PheromoneController pheromoneController = new PheromoneController(gameModel.getPheromoneUsageProvider(),
                cameraView);
        SpeedController speedController = new SpeedController(gameModel);
        HudController hudController = new HudController(hudView, gameModel.getAntTypeRegistry(),
                gameModel.getEggManager(), pheromoneController, speedController,
                uiFactory, gameModel.getTimeProvider(), gameModel.getColonyDataProvider(), gameModel.getEggManager());

        // 5. Wire Input
        inputManager.addProcessor(cameraController);
        inputManager.addProcessor(hudView.getStage());
        inputManager.addProcessor(pheromoneController);

        // 6. Wire Listeners
        viewportListener.addObserver(cameraView);
        viewportListener.addObserver(fogRenderer);
        gameModel.getFogProvider().addObserver(fogRenderer);

        return new GameScreen(
                this,
                game,
                gameModel,
                cameraView,
                worldRenderer,
                pheromoneView,
                hudView,
                textureRegistry,
                uiFactory,
                viewportListener,
                cameraController,
                hudController);
    }

    private CameraModel createCameraModel(GridPoint2 mapSize) {

        Rectangle worldBounds = new Rectangle(
                -mapSize.x / 2f, -mapSize.y / 2f,
                mapSize.x, mapSize.y);
        CameraConstraints constraints = new CameraConstraints(
                worldBounds, GameConfiguration.MIN_ZOOM, GameConfiguration.MAX_ZOOM);
        return new CameraModel(constraints);
    }

    // TODO - Antigravity: Long method (47 lines) - break into createSimulation(),
    // createWorldAndTerrain(), createEntitySystem()
    private GameModel createGameModel(GridPoint2 mapSize) {
        AntTypeRegistry antTypeRegistry = createAntTypeRegistry();

        TerrainGenerator terrainGenerator = TerrainFactory.createStandardPerlinGenerator(
                gameConfiguration.seed());
        // TODO: break this down
        SimulationManager simulationManager = new SimulationManager();
        TimeCycle timeCycle = new TimeCycle(1f / GameConfiguration.TICKS_PER_MINUTE);
        simulationManager.addUpdateObserver(timeCycle);

        DestructionListener destructionListener = new DestructionListener();
        EntityManager entityManager = new EntityManager();
        simulationManager.addUpdateObserver(entityManager);
        destructionListener.addEntityDeathObserver(entityManager);

        StructureManager structureManager = new StructureManager();
        simulationManager.addUpdateObserver(structureManager);

        ResourceManager resourceManager = new ResourceManager(entityManager, structureManager);
        simulationManager.addUpdateObserver(resourceManager);

        WorldMap worldMap = new WorldMap(mapSize.x, mapSize.y, terrainGenerator);

        // Termite target priority
        HashMap<AttackCategory, Integer> termiteTargetPriority = new HashMap<>();
        termiteTargetPriority.put(AttackCategory.WORKER_ANT, 2);

        EnemyFactory enemyFactory = new EnemyFactory(entityManager, structureManager, destructionListener,
                termiteTargetPriority);
        FogManager fogManager = new FogManager(entityManager, worldMap);
        simulationManager.addUpdateObserver(fogManager);
        PheromoneGridConverter pheromoneGridConverter = new PheromoneGridConverter(4);

        // Ant target priority
        HashMap<AttackCategory, Integer> antTargetPriority = new HashMap<>();
        antTargetPriority.put(AttackCategory.TERMITE, 2);

        PheromoneManager pheromoneManager = new PheromoneManager(new GridPoint2(0, 0), pheromoneGridConverter, 4);
        AntFactory antFactory = new AntFactory(pheromoneManager, worldMap, entityManager,
                destructionListener, structureManager, antTargetPriority);

        ResourceNodeFactory resourceNodeFactory = new ResourceNodeFactory(structureManager);
        Colony colony = createColony(timeCycle, entityManager, structureManager,
                gameConfiguration.startResources());

        EggManager eggManager = new EggManager(antTypeRegistry, antFactory, colony, entityManager);
        timeCycle.addTimeObserver(eggManager);

        spawnInitialAnts(entityManager, colony, antFactory, antTypeRegistry);
        spawnTerrainStructures(resourceNodeFactory, worldMap);

        WaveManager waveManager = new WaveManager(enemyFactory, entityManager);
        timeCycle.addTimeObserver(waveManager);

        return new GameModel(simulationManager, timeCycle, fogManager, colony,
                pheromoneManager, worldMap, antTypeRegistry, structureManager, entityManager, eggManager);
    }

    public void spawnInitialAnts(EntityManager entityManager, Home home, AntFactory antFactory,
            AntTypeRegistry antTypeRegistry) {
        AntType type = antTypeRegistry.get(gameConfiguration.antType());
        for (int i = 0; i < gameConfiguration.startAnts(); i++) {
            Ant ant = antFactory.createAnt(home, type);
            entityManager.addEntity(ant);
        }
    }

    /**
     * Spawns structures determined by terrain generation features.
     */
    private void spawnTerrainStructures(ResourceNodeFactory resourceNodeFactory, MapProvider map) {
        for (StructureSpawn spawn : map.getStructureSpawns()) {
            if ("resource_node".equals(spawn.getType())) {
                Vector2 structurePos = map.tileToWorld(spawn.getPosition());

                resourceNodeFactory.createResourceNode(structurePos, spawn);
            }
            // Add other structure types here
        }
    }

    private Colony createColony(TimeCycle timeCycle,
            EntityQuery entityQuery, StructureManager structureManager,
            int initialFood) {
        Colony colony = new Colony(new GridPoint2(0, 0), entityQuery,
                initialFood);
        structureManager.addStructure(colony);
        timeCycle.addTimeObserver(colony);
        return colony;
    }

    private CameraView createCameraView(CameraModel cameraModel) {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float aspectRatio = screenWidth == 0 ? 1f : screenHeight / screenWidth;

        return new CameraView(
                cameraModel,
                GameConfiguration.WORLD_VIEWPORT_WIDTH,
                GameConfiguration.WORLD_VIEWPORT_WIDTH * aspectRatio);
    }

    /**
     * Initializes and registers all ant types in the registry.
     * This must be called before creating GameModel to ensure ant types are
     * available.
     */
    private AntTypeRegistry createAntTypeRegistry() {
        // TODO: move out somewhere that's not here
        AntTypeRegistry registry = new AntTypeRegistry();

        // Scout: High speed, low HP, 0 capacity, cheap/fast to hatch
        registry.register(AntType.with()
                .id("scout")
                .displayName("Scout")
                .foodCost(5)
                .developmentTicks(30)
                .maxHealth(4f)
                .moveSpeed(8f)
                .carryCapacity(0)
                .textureName("scout")
                .allowedPheromones(Set.of(PheromoneType.EXPLORE))
                .homeBias(0.05f) // Low home bias - scouts wander far
                .build());

        // Soldier: Low speed, high HP, 0 capacity, expensive
        registry.register(AntType.with()
                .id("soldier")
                .displayName("Soldier")
                .foodCost(40)
                .developmentTicks(300)
                .maxHealth(20f)
                .moveSpeed(2f)
                .carryCapacity(0)
                .textureName("ant")
                .allowedPheromones(Set.of(PheromoneType.ATTACK))
                .homeBias(0.3f)
                .build());

        // Worker: Medium speed, medium HP, some capacity
        registry.register(AntType.with()
                .id("worker")
                .displayName("Worker")
                .foodCost(10)
                .developmentTicks(60)
                .maxHealth(6f)
                .moveSpeed(5f)
                .carryCapacity(10)
                .textureName("ant")
                .allowedPheromones(Set.of(PheromoneType.GATHER))
                .homeBias(0.1f)
                .build());

        return registry;
    }
}
