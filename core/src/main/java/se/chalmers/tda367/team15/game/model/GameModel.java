package se.chalmers.tda367.team15.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.egg.EggManager;
import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;
import se.chalmers.tda367.team15.game.model.fog.FogProvider;
import se.chalmers.tda367.team15.game.model.fog.FogManager;
import se.chalmers.tda367.team15.game.model.interfaces.ColonyUsageProvider;
import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.managers.PheromoneManager;
import se.chalmers.tda367.team15.game.model.managers.StructureManager;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceNode;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;
import se.chalmers.tda367.team15.game.model.world.WorldMap;
import se.chalmers.tda367.team15.game.model.world.terrain.StructureSpawn;

public class GameModel {
    private final ColonyUsageProvider colonyUsageProvider;
    // TODO: Fix
    private final TimeCycle timeCycle;
    private final FogManager fogManager;
    private final SimulationProvider simulationProvider;
    private final PheromoneManager pheromoneManager;
    private final WorldMap worldMap;
    private final AntTypeRegistry antTypeRegistry;
    private final StructureManager structureManager;
    private final EntityQuery entityQuery;

    public GameModel(SimulationProvider simulationProvider, TimeCycle timeCycle,
            FogManager fogManager, ColonyUsageProvider colonyUsageProvider, PheromoneManager pheromoneManager,
            WorldMap worldMap, AntTypeRegistry antTypeRegistry, StructureManager structureManager, EntityQuery entityQuery) {
        this.simulationProvider = simulationProvider;
        this.colonyUsageProvider = colonyUsageProvider;
        this.timeCycle = timeCycle;
        this.fogManager = fogManager;
        this.pheromoneManager = pheromoneManager;
        this.worldMap = worldMap;
        this.antTypeRegistry = antTypeRegistry;
        this.structureManager = structureManager;
        this.entityQuery = entityQuery;
        // Spawn structures based on terrain generation features
        spawnTerrainStructures();
    }

    public Iterable<Drawable> getDrawables() {
        List<Drawable> allDrawables = new ArrayList<>(structureManager.getStructures());
        allDrawables.addAll(entityQuery.getEntitiesOfType(Entity.class));
        return Collections.unmodifiableList(allDrawables);
    }

    /**
     * Spawns structures determined by terrain generation features.
     */
    private void spawnTerrainStructures() {
        List<StructureSpawn> spawns = worldMap.getStructureSpawns();

        for (StructureSpawn spawn : spawns) {
            if ("resource_node".equals(spawn.getType())) {
                Vector2 worldPos = worldMap.tileToWorld(spawn.getPosition());
                GridPoint2 worldGridPos = new GridPoint2((int) worldPos.x, (int) worldPos.y);

                structureManager.addStructure(new ResourceNode(
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
        return pheromoneManager.getConverter();
    }

    // --- FACADE METHODS (Actions) ---

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
        return timeCycle.getGameTime();
    }

    public FogProvider getFogProvider() {
        return fogManager;
    }

    public AntTypeRegistry getAntTypeRegistry() {
        return antTypeRegistry;
    }

    public PheromoneManager getPheromoneManager() {
        return pheromoneManager;
    }

    public WorldMap getWorldMap() {
        return worldMap;
    }

    public GridPoint2 getWorldSize() {
        return getWorldMap().getSize();
    }

    public int getTotalDays() {
        return timeCycle.getTotalDays();
    }

    public EggManager getEggManager() {
        return colonyUsageProvider.getEggManager();
    }

    // TODO: Fix this
    public TimeCycle getTimeCycle() {
        return timeCycle;
    }

    public int getTotalAnts() {
        return entityQuery.getEntitiesOfType(Ant.class).size();
    }
}
