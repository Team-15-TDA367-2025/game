package se.chalmers.tda367.team15.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.interfaces.Updatable;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.interfaces.TimeObserver;
import se.chalmers.tda367.team15.game.model.structure.Colony;
import se.chalmers.tda367.team15.game.model.structure.Structure;
import se.chalmers.tda367.team15.game.model.structure.resource.Resource;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceSystem;

public class GameWorld implements EntityDeathObserver, StructureDeathObserver {
    private Colony colony = new Colony(new GridPoint2(0, 0));
    private final PheromoneSystem pheromoneSystem;
    private List<Entity> worldEntities; // Floating positions and can move around.
    private List<Structure> structures; // Integer positions and fixed in place.
    private List<Resource> resources;
    private ResourceSystem resourceSystem;
    private final FogSystem fogSystem;
    private final FogOfWar fogOfWar;
    private DestructionListener destructionListener;
    private TimeCycle timeCycle;
    private List<TimeObserver> timeObservers;
    private float tickAccumulator = 0f;
    private float secondsPerTick;
    private static GameWorld gameWorld;

    public GameWorld(TimeCycle timeCycle, int mapWidth, int mapHeight, float tileSize) {
        fogOfWar = new FogOfWar(mapWidth, mapHeight, tileSize);
        fogSystem = new FogSystem(fogOfWar);
        this.worldEntities = new ArrayList<>();
        this.structures = new ArrayList<>();
        this.resources = new ArrayList<>();
        this.resourceSystem = new ResourceSystem();
        this.timeObservers = new ArrayList<>();
        this.timeCycle = timeCycle;
        this.secondsPerTick = 60f / timeCycle.getTicksPerMinute();
        destructionListener = DestructionListener.getInstance();
        destructionListener.addEntityDeathObserver(this);
        destructionListener.addStructureDeathObserver(this);
        pheromoneSystem = new PheromoneSystem(new GridPoint2(0, 0));

    }

    public static GameWorld createInstance(TimeCycle timeCycle, int mapWidth, int mapHeight, float tileSize) {
        gameWorld = new GameWorld(timeCycle, mapWidth, mapHeight, tileSize);
        return gameWorld;
    }

    public static GameWorld getInstance() {
        if (gameWorld == null) {
            throw new IllegalStateException("GameWorld must be created with createInstance() before used");
        }
        return gameWorld;
    }

    public Colony getColony() {
        return colony;
    }

    public List<Structure> getStructures() {
        return Collections.unmodifiableList(structures);

    }

    public List<Entity> getEntities() {
        List<Entity> allEntities = new ArrayList<>(worldEntities);
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

    public PheromoneSystem getPheromoneSystem() {
        return pheromoneSystem;
    }

    public FogOfWar getFog() {
        return fogOfWar;
    }

    public void addTimeObserver(TimeObserver observer) {
        timeObservers.add(observer);
    }

    public void removeTimeObserver(TimeObserver observer) {
        timeObservers.remove(observer);
    }

    private void notifyTimeObservers() {
        for (TimeObserver observer : timeObservers) {
            observer.onTimeUpdate(timeCycle);
        }
    }

    private List<Updatable> getUpdatables() {
        List<Updatable> updatables = new ArrayList<>();
        updatables.addAll(getEntities());
        updatables.addAll(structures);
        return updatables;
    }

    public void update(float deltaTime) {
        List<Entity> entities = getEntities();
        tickAccumulator += deltaTime; // add real seconds
        while (tickAccumulator >= secondsPerTick) {
            timeCycle.tick();
            notifyTimeObservers();
            tickAccumulator -= secondsPerTick; // remove the processed time
        }

        List<Updatable> updateTheseEntities = getUpdatables();
        Updatable spotlightedUpdateable;
        while (!updateTheseEntities.isEmpty()) {
            // Update fog after movement
            fogSystem.updateFog(entities);
            resourceSystem.update(colony, entities, resources);
        }
    }

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
        resources.add(resource);
        resourceSystem.addResource(resource);
    }

    public void removeStructure(Structure s) {
        structures.remove(s);
    }

    @Override
    public void onStructureDeath(Structure s) {
        removeStructure(s);
    }
}
