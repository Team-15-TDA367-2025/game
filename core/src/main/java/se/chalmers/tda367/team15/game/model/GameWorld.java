package se.chalmers.tda367.team15.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.StructureDeathObserver;
import se.chalmers.tda367.team15.game.model.managers.StructureManager;
import se.chalmers.tda367.team15.game.model.structure.Structure;
import se.chalmers.tda367.team15.game.model.structure.resource.Resource;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceNode;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceSystem;
import se.chalmers.tda367.team15.game.model.world.TerrainGenerator;

public class GameWorld implements StructureDeathObserver {
    private final StructureManager structureManager;
    private final ResourceSystem resourceSystem;
    private final EntityQuery entityQuery;

    public GameWorld(int mapWidth, int mapHeight, TerrainGenerator generator, EntityQuery entityQuery, StructureManager structureManager, ResourceSystem resourceSystem) {
        this.entityQuery = entityQuery;
        this.structureManager = structureManager;
        this.resourceSystem = resourceSystem;
    }
    
    public List<Structure> getStructures() {
        return Collections.unmodifiableList(structureManager.getStructures());
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
        List<Drawable> allDrawables = new ArrayList<>(structureManager.getStructures());
        allDrawables.addAll(getEntities());
        return Collections.unmodifiableList(allDrawables);
    }

    public void addStructure(Structure structure) {
        structureManager.addStructure(structure);
    }

    public void addResource(Resource resource) {
        structureManager.addStructure(resource);
        resourceSystem.addResource(resource);
    }

    public void addResourceNode(ResourceNode resourceNode) {
        structureManager.addStructure(resourceNode);
        resourceSystem.addResourceNode(resourceNode);
    }

    public void removeStructure(Structure s) {
        structureManager.removeStructure(s);
    }

    @Override
    public void onStructureDeath(Structure s) {
        removeStructure(s);
    }
}
