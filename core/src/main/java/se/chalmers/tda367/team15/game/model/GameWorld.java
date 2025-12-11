package se.chalmers.tda367.team15.game.model;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.interfaces.EntityDeathObserver;
import se.chalmers.tda367.team15.game.model.interfaces.StructureDeathObserver;
import se.chalmers.tda367.team15.game.model.interfaces.TimeObserver;
import se.chalmers.tda367.team15.game.model.interfaces.Updatable;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.structure.Colony;
import se.chalmers.tda367.team15.game.model.structure.Structure;
import se.chalmers.tda367.team15.game.model.structure.resource.Resource;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceNode;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceSystem;
import se.chalmers.tda367.team15.game.model.world.TerrainGenerator;
import se.chalmers.tda367.team15.game.model.world.WorldMap;

public class GameWorld implements EntityDeathObserver, StructureDeathObserver {
    private final Colony colony;
    private final PheromoneSystem pheromoneSystem;
    private List<Entity> worldEntities; // Floating positions and can move around.
    private List<Structure> structures; // Integer positions and fixed in place.
    private ResourceSystem resourceSystem;
    private final WorldMap worldMap;
    private final FogSystem fogSystem;
    private final FogOfWar fogOfWar;
    private DestructionListener destructionListener;

    public GameWorld(TimeCycle timeCycle, SimulationHandler simulationHandler , int mapWidth, int mapHeight, TerrainGenerator generator) {
        this.worldEntities = new ArrayList<>();
        this.structures = new ArrayList<>();
        this.worldMap = new WorldMap(mapWidth, mapHeight, generator);
        this.fogOfWar = new FogOfWar(worldMap);
        this.fogSystem = new FogSystem(this,simulationHandler,fogOfWar, worldMap);
        pheromoneSystem = new PheromoneSystem(new GridPoint2(0, 0), new PheromoneGridConverter(4));
        this.resourceSystem = new ResourceSystem(this, simulationHandler);
        destructionListener = DestructionListener.getInstance();

        destructionListener.addEntityDeathObserver(this);
        destructionListener.addStructureDeathObserver(this);
        this.colony = new Colony(new GridPoint2(0,0),this,timeCycle,simulationHandler);
        structures.add(colony);

        //simulationHandler.addUpdateObserver(this);
    }

    public Colony getColony() {
        return colony;
    }

    public List<Structure> getStructures() {
        return Collections.unmodifiableList(structures);
    }

    public List<Entity> getEntities() {
        List<Entity> allEntities = new ArrayList<>();
        allEntities.addAll(worldEntities);
        for (Structure structure : structures) {
            allEntities.addAll(structure.getSubEntities());
        }
        return Collections.unmodifiableList(allEntities);
    }

    // TODO: Clean up this, we already know entities are subentities to colony
    public List<Ant> getAnts() {
        List<Ant> ants = new ArrayList<>();
        for (Entity entity : getEntities()) {
            if (entity instanceof Ant) {
                ants.add((Ant) entity);
            }
        }
        return Collections.unmodifiableList(ants);
    }

    public Iterable<Drawable> getDrawables() {
        List<Drawable> allDrawables = new ArrayList<>(structures);
        allDrawables.addAll(getEntities());
        return Collections.unmodifiableList(allDrawables);
    }

    public PheromoneSystem getPheromoneSystem() {
        return pheromoneSystem;
    }

    public FogOfWar getFog() {
        return fogOfWar;
    }

    public WorldMap getWorldMap() {
        return worldMap;
    }

    public List<Updatable> getUpdatables() {
        List<Updatable> updatables = new ArrayList<>();
        updatables.addAll(getEntities());
        updatables.addAll(structures);
        return updatables;
    }

    /*
    public void update() {
        List<Entity> entities = getEntities();
        // Update fog after movement

    }
    */

    public void addEntity(Entity entity) {
        worldEntities.add(entity);
    }

    public void removeEntity(Entity e) {
        worldEntities.remove(e);
    }

    @Override
    public void onEntityDeath(Entity e) {
        removeEntity(e);
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
