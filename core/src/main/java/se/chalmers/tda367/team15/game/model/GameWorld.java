package se.chalmers.tda367.team15.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.structure.Colony;
import se.chalmers.tda367.team15.game.model.structure.Structure;
import se.chalmers.tda367.team15.game.model.structure.resource.Resource;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceSystem;

public class GameWorld {
    private final Colony colony;
    private List<Entity> entities; // Floating positions and can move around.
    private List<Structure> structures; // Integer positions and fixed in place.
    private List<Resource> resources;
    private final FogSystem fogSystem;
    private final FogOfWar fogOfWar;
    private TimeCycle timeCycle;
    private ResourceSystem resourceSystem;

    public GameWorld(Colony colony, TimeCycle timeCycle, int mapWidth, int mapHeight, float tileSize) {
        this.colony = colony;
        fogOfWar = new FogOfWar(mapWidth, mapHeight, tileSize);
        fogSystem = new FogSystem(fogOfWar);
        this.entities = new ArrayList<>();
        this.structures = new ArrayList<>();
        this.resources = new ArrayList<>();
        this.timeCycle = timeCycle;
        this.resourceSystem = new ResourceSystem();
    }

    public List<Entity> getEntities() {
        List<Entity> allEntities = new ArrayList<>(entities);
        for (Structure structure : structures) {
            allEntities.addAll(structure.getSubEntities());
        }
        return Collections.unmodifiableList(allEntities);
    }

    public Iterable<Drawable> getDrawables() {
        List<Drawable> allDrawables = new ArrayList<>(structures);
        allDrawables.addAll(resources);
        allDrawables.addAll(getEntities());
        return Collections.unmodifiableList(allDrawables);
    }

    public FogOfWar getFog() {
        return fogOfWar;
    }

    public void update(float deltaTime) {
        for (Entity e : entities) {
            e.update(deltaTime);
        }

        for (Structure structure : structures) {
            structure.update(deltaTime);
        }
        // Update fog after movement
        fogSystem.updateFog(entities);
        resourceSystem.update(colony, entities, resources);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void addStructure(Structure structure) {
        structures.add(structure);
    }

    public void addResource(Resource resource) {
        resources.add(resource);
        resourceSystem.addResource(resource);
    }
}