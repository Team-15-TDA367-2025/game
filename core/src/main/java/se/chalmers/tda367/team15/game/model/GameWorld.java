package se.chalmers.tda367.team15.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.StructureDeathObserver;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.structure.Colony;
import se.chalmers.tda367.team15.game.model.structure.Structure;
import se.chalmers.tda367.team15.game.model.structure.resource.Resource;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceNode;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceSystem;
import se.chalmers.tda367.team15.game.model.world.TerrainGenerator;
import se.chalmers.tda367.team15.game.model.world.WorldMap;

public class GameWorld implements StructureDeathObserver {
    private Colony colony;
    private final PheromoneSystem pheromoneSystem;
    private List<Structure> structures;
    private ResourceSystem resourceSystem;
    private final WorldMap worldMap;
    private EntityQuery entityQuery;

    public GameWorld(SimulationHandler simulationHandler, int mapWidth, int mapHeight, TerrainGenerator generator) {
        this.structures = new ArrayList<>();
        this.worldMap = new WorldMap(mapWidth, mapHeight, generator);
        pheromoneSystem = new PheromoneSystem(new GridPoint2(0, 0), new PheromoneGridConverter(4), 4);
        this.resourceSystem = new ResourceSystem(this, simulationHandler);

        DestructionListener.getInstance().addStructureDeathObserver(this);
    }

    public void setEntityQuery(EntityQuery entityQuery) {
        this.entityQuery = entityQuery;
    }

    public void setColony(Colony colony) {
        this.colony = colony;
        structures.add(colony);
    }

    public Colony getColony() {
        return colony;
    }

    public List<Structure> getStructures() {
        return Collections.unmodifiableList(structures);
    }

    public List<Entity> getEntities() {
        if (entityQuery == null) {
            return Collections.emptyList();
        }
        return entityQuery.getEntitiesOfType(Entity.class);
    }

    public List<Ant> getAnts() {
        if (entityQuery == null) {
            return Collections.emptyList();
        }
        return entityQuery.getEntitiesOfType(Ant.class);
    }

    public Iterable<Drawable> getDrawables() {
        List<Drawable> allDrawables = new ArrayList<>(structures);
        allDrawables.addAll(getEntities());
        return Collections.unmodifiableList(allDrawables);
    }

    public PheromoneSystem getPheromoneSystem() {
        return pheromoneSystem;
    }

    public WorldMap getWorldMap() {
        return worldMap;
    }

    public void addStructure(Structure structure) {
        structures.add(structure);
    }

    public void addResource(Resource resource) {
        structures.add(resource);
        resourceSystem.addResource(resource);
    }

    public void addResourceNode(ResourceNode resourceNode) {
        structures.add(resourceNode);
        resourceSystem.addResourceNode(resourceNode);
    }

    public void removeStructure(Structure s) {
        structures.remove(s);
    }

    @Override
    public void onStructureDeath(Structure s) {
        removeStructure(s);
    }
}
