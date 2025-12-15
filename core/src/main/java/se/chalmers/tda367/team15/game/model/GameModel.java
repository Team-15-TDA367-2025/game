package se.chalmers.tda367.team15.game.model;

import java.util.List;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;
import se.chalmers.tda367.team15.game.model.entity.termite.Termite;
import se.chalmers.tda367.team15.game.model.egg.EggManager;
import se.chalmers.tda367.team15.game.model.fog.FogProvider;
import se.chalmers.tda367.team15.game.model.fog.FogSystem;
import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.structure.Colony;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceNode;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;
import se.chalmers.tda367.team15.game.model.world.WorldMap;
import se.chalmers.tda367.team15.game.model.world.terrain.StructureSpawn;

public class GameModel {
    private final GameWorld world;
    @SuppressWarnings("unused")
    private final WaveManager waveManager;
    private final TimeCycle timeCycle;
    private final EntityManager entityManager;
    private final FogSystem fogSystem;
    private final EnemyFactory enemyFactory;
    private final AntFactory antFactory;
    private final Colony colony;
    private final EggManager eggManager;
    private final SimulationProvider simulationProvider;

    public GameModel(SimulationProvider simulationProvider, TimeCycle timeCycle, GameWorld gameWorld,
            FogSystem fogSystem, EntityManager entityManager, EggManager eggManager) {
        this.simulationProvider = simulationProvider;
        this.world = gameWorld;
        this.timeCycle = timeCycle;
        this.waveManager = new WaveManager(this.timeCycle, this);
        this.entityManager = entityManager;
        this.eggManager = eggManager;
        this.colony = new Colony(new GridPoint2(0, 0), timeCycle, entityManager, this.eggManager);
        gameWorld.setColony(colony);
        this.antFactory = new AntFactory(gameWorld.getPheromoneSystem(), colony, gameWorld);
        this.fogSystem = fogSystem;
        colony.setAntHatchListener(this::onAntHatch);
        this.enemyFactory = new EnemyFactory(gameWorld);

        // Spawn structures based on terrain generation features
        spawnTerrainStructures();
    }

    private void onAntHatch(AntType type) {
        Ant ant = antFactory.createAnt(type);
        entityManager.addEntity(ant);
    }

    /**
     * Spawns an initial ant (used at game start).
     */
    public void spawnInitialAnts() {
        AntTypeRegistry registry = AntTypeRegistry.getInstance();
        AntType type = registry.get("worker");
        Ant ant = antFactory.createAnt(type);
        entityManager.addEntity(ant);
    }

    /**
     * Spawns structures determined by terrain generation features.
     */
    private void spawnTerrainStructures() {
        WorldMap worldMap = world.getWorldMap();
        List<StructureSpawn> spawns = worldMap.getStructureSpawns();

        for (StructureSpawn spawn : spawns) {
            if ("resource_node".equals(spawn.getType())) {
                Vector2 worldPos = worldMap.tileToWorld(spawn.getPosition());
                GridPoint2 worldGridPos = new GridPoint2((int) worldPos.x, (int) worldPos.y);

                world.addResourceNode(new ResourceNode(
                        worldGridPos,
                        "node",
                        1,
                        ResourceType.FOOD,
                        10,
                        20));
            }
            // Add other structure types here
        }
    }

    public PheromoneGridConverter getPheromoneGridConverter() {
        return world.getPheromoneSystem().getConverter();
    }

    // --- FACADE METHODS (Actions) ---

    public void spawnTermite(Vector2 position) {
        Termite termite = enemyFactory.createTermite(position);
        entityManager.addEntity(termite);
    }

    public void setTimeFast() {
        simulationProvider.setTimeFast();
    }

    public void setTimeNormal() {
        simulationProvider.setTimeNormal();
    }

    public void setTimePaused() {
        simulationProvider.setTimePaused();
    }

    public void update() {
        simulationProvider.handleSimulation();
    }

    public boolean isDay() {
        return timeCycle.getIsDay();
    }

    public TimeCycle.GameTime getGameTime() {
        return simulationProvider.getTimeCycle().getGameTime();
    }

    public Iterable<Drawable> getDrawables() {
        return world.getDrawables();
    }

    public FogProvider getFogProvider() {
        return fogSystem;
    }

    public PheromoneSystem getPheromoneSystem() {
        return world.getPheromoneSystem();
    }

    public Colony getColony() {
        return colony;
    }

    public WorldMap getWorldMap() {
        return world.getWorldMap();
    }

    public GridPoint2 getWorldSize() {
        return getWorldMap().getSize();
    }

    public int getTotalDays() {
        return simulationProvider.getTimeCycle().getTotalDays();
    }

    public int getTotalAnts() {
        return world.getAnts().size();
    }

}
