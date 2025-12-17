package se.chalmers.tda367.team15.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import se.chalmers.tda367.team15.game.model.interfaces.TimeCycleDataProvider;
import se.chalmers.tda367.team15.game.model.managers.PheromoneManager;
import se.chalmers.tda367.team15.game.model.managers.StructureManager;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceNode;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;
import se.chalmers.tda367.team15.game.model.world.MapProvider;
import se.chalmers.tda367.team15.game.model.world.terrain.StructureSpawn;

public class GameModel {
    private final ColonyUsageProvider colonyUsageProvider;
    // TODO: Fix
    private final TimeCycleDataProvider timeProvider;
    private final FogManager fogManager;
    private final SimulationProvider simulationProvider;
    private final PheromoneManager pheromoneManager;
    private final MapProvider mapProvider;
    private final AntTypeRegistry antTypeRegistry;
    private final StructureManager structureManager;
    private final EntityQuery entityQuery;

    public GameModel(SimulationProvider simulationProvider, TimeCycleDataProvider timeProvider,
            FogManager fogManager, ColonyUsageProvider colonyUsageProvider, PheromoneManager pheromoneManager,
            MapProvider mapProvider, AntTypeRegistry antTypeRegistry, StructureManager structureManager, EntityQuery entityQuery) {
        this.simulationProvider = simulationProvider;
        this.colonyUsageProvider = colonyUsageProvider;
        this.timeProvider = timeProvider;
        this.fogManager = fogManager;
        this.pheromoneManager = pheromoneManager;
        this.mapProvider = mapProvider;
        this.antTypeRegistry = antTypeRegistry;
        this.structureManager = structureManager;
        this.entityQuery = entityQuery;
    }

    public Iterable<Drawable> getDrawables() {
        List<Drawable> allDrawables = new ArrayList<>(structureManager.getStructures());
        allDrawables.addAll(entityQuery.getEntitiesOfType(Entity.class));
        return Collections.unmodifiableList(allDrawables);
    }

    public ColonyUsageProvider getColonyUsageProvider() {
        return colonyUsageProvider;
    }

    public TimeCycleDataProvider getTimeProvider() {
        return timeProvider;
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

    public FogProvider getFogProvider() {
        return fogManager;
    }

    public AntTypeRegistry getAntTypeRegistry() {
        return antTypeRegistry;
    }

    public PheromoneManager getPheromoneManager() {
        return pheromoneManager;
    }

    public MapProvider getMapProvider() {
        return mapProvider;
    }

    public EggManager getEggManager() {
        return colonyUsageProvider.getEggManager();
    }

    public int getTotalAnts() {
        return entityQuery.getEntitiesOfType(Ant.class).size();
    }
}
