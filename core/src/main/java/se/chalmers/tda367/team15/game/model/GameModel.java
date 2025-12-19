package se.chalmers.tda367.team15.game.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.GameObject;
import se.chalmers.tda367.team15.game.model.interfaces.providers.ColonyDataProvider;
import se.chalmers.tda367.team15.game.model.interfaces.providers.PheromoneUsageProvider;
import se.chalmers.tda367.team15.game.model.interfaces.providers.SimulationProvider;
import se.chalmers.tda367.team15.game.model.interfaces.providers.StructureModificationProvider;
import se.chalmers.tda367.team15.game.model.interfaces.providers.TimeCycleDataProvider;
import se.chalmers.tda367.team15.game.model.managers.egg.EggManager;
import se.chalmers.tda367.team15.game.model.managers.fog.FogProvider;
import se.chalmers.tda367.team15.game.model.world.MapProvider;

public class GameModel {
    private final ColonyDataProvider colonyDataProvider;

    private final TimeCycleDataProvider timeProvider;
    private final FogProvider fogProvider;
    private final SimulationProvider simulationProvider;
    private final PheromoneUsageProvider pheromoneUsageProvider;
    private final MapProvider mapProvider;
    private final AntTypeRegistry antTypeRegistry;
    private final StructureModificationProvider structureModificationProvider;
    private final EntityQuery entityQuery;
    private final EggManager eggManager;

    public GameModel(SimulationProvider simulationProvider, TimeCycleDataProvider timeProvider,
            FogProvider fogProvider, ColonyDataProvider colonyDataProvider,
            PheromoneUsageProvider pheromoneUsageProvider,
            MapProvider mapProvider, AntTypeRegistry antTypeRegistry,
            StructureModificationProvider structureModificationProvider,
            EntityQuery entityQuery, EggManager eggManager) {
        this.simulationProvider = simulationProvider;
        this.colonyDataProvider = colonyDataProvider;
        this.timeProvider = timeProvider;
        this.fogProvider = fogProvider;
        this.pheromoneUsageProvider = pheromoneUsageProvider;
        this.mapProvider = mapProvider;
        this.antTypeRegistry = antTypeRegistry;
        this.structureModificationProvider = structureModificationProvider;
        this.entityQuery = entityQuery;
        this.eggManager = eggManager;
    }

    public Collection<GameObject> getDrawables() {
        List<GameObject> allDrawables = new ArrayList<>(structureModificationProvider.getStructures());
        allDrawables.addAll(entityQuery.getEntitiesOfType(Entity.class));
        return Collections.unmodifiableList(allDrawables);
    }

    public ColonyDataProvider getColonyDataProvider() {
        return colonyDataProvider;
    }

    public TimeCycleDataProvider getTimeProvider() {
        return timeProvider;
    }

    public PheromoneUsageProvider getPheromoneUsageProvider() {
        return pheromoneUsageProvider;
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
        return fogProvider;
    }

    public AntTypeRegistry getAntTypeRegistry() {
        return antTypeRegistry;
    }

    public MapProvider getMapProvider() {
        return mapProvider;
    }

    public EggManager getEggManager() {
        return eggManager;
    }

    public int getTotalAnts() {
        return entityQuery.getEntitiesOfType(Ant.class).size();
    }
}
