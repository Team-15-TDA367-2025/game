package se.chalmers.tda367.team15.game.model;

import java.util.List;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.egg.EggManager;
import se.chalmers.tda367.team15.game.model.entity.termite.Termite;
import se.chalmers.tda367.team15.game.model.fog.FogProvider;
import se.chalmers.tda367.team15.game.model.fog.FogSystem;
import se.chalmers.tda367.team15.game.model.interfaces.ColonyUsageProvider;
import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceNode;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;
import se.chalmers.tda367.team15.game.model.world.WorldMap;
import se.chalmers.tda367.team15.game.model.world.terrain.StructureSpawn;

public class GameModel {
    private final GameWorld world;
    private final ColonyUsageProvider colonyUsageProvider;
    // TODO: Fix
    private final TimeCycle timeCycle;
    private final EntityManager entityManager;
    private final FogSystem fogSystem;
    private final EnemyFactory enemyFactory;
    private final SimulationProvider simulationProvider;

    public GameModel(SimulationProvider simulationProvider, TimeCycle timeCycle, GameWorld gameWorld,
            FogSystem fogSystem, EntityManager entityManager, ColonyUsageProvider colonyDataProvider, EnemyFactory enemyFactory) {
        this.simulationProvider = simulationProvider;
        this.world = gameWorld;
        this.colonyUsageProvider = colonyDataProvider;
        this.timeCycle = timeCycle;
        this.entityManager = entityManager;
        this.fogSystem = fogSystem;
        this.enemyFactory = enemyFactory;
        // Spawn structures based on terrain generation features
        spawnTerrainStructures();
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

    public ColonyUsageProvider getColonyUsageProvider() {
        return colonyUsageProvider;
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

    public WorldMap getWorldMap() {
        return world.getWorldMap();
    }

    public GridPoint2 getWorldSize() {
        return getWorldMap().getSize();
    }

    public int getTotalDays() {
        return simulationProvider.getTimeCycle().getTotalDays();
    }

    public EggManager getEggManager() {
        return colonyUsageProvider.getEggManager();
    }

    // TODO: Fix this
    public TimeCycle getTimeCycle() {
        return timeCycle;
    }

    public int getTotalAnts() {
        return world.getAnts().size();
    }
}
