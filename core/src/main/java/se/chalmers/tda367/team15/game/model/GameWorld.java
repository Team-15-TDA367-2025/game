package se.chalmers.tda367.team15.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.interfaces.Updatable;
import se.chalmers.tda367.team15.game.model.interfaces.TimeObserver;
import se.chalmers.tda367.team15.game.model.structure.Colony;
import se.chalmers.tda367.team15.game.model.structure.Structure;


public class GameWorld implements EntityDeathObserver, StructureDeathObserver {
    private List<Entity> entities; // Floating positions and can move around.
    private List<Structure> structures; // Integer positions and fixed in place.
    private final FogSystem fogSystem;
    private final FogOfWar fogOfWar;
    private DestructionListener destructionListener;
    private TimeCycle timeCycle;
    private List<TimeObserver> timeObservers;
    private final WaveManager waveManager;
    private static GameWorld gameWorld;

    private GameWorld( int mapWidth, int mapHeight, float tileSize) {
        fogOfWar = new FogOfWar(mapWidth, mapHeight, tileSize);
        fogSystem = new FogSystem(fogOfWar);
        this.entities = new ArrayList<>();
        this.structures = new ArrayList<>();
        this.timeObservers = new ArrayList<>();
        this.timeCycle = new TimeCycle();
        destructionListener = DestructionListener.getInstance();
        destructionListener.addEntityDeathObserver(this);
        destructionListener.addStructureDeathObserver(this);
        waveManager = new WaveManager();
        addTimeObserver(waveManager);
    }

    public static GameWorld createInstance( int mapWidth, int mapHeight, float tileSize) {
        gameWorld=new GameWorld(mapWidth,mapHeight,tileSize);
        return gameWorld;
    }

    public static GameWorld getInstance() {
        if(gameWorld == null) {
            throw new IllegalStateException("GameWorld must be created with createInstance() before used");
        }
        return gameWorld;
    }

    public List<Structure> getStructures() {
        return Collections.unmodifiableList(structures);


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
        allDrawables.addAll(getEntities());
        return Collections.unmodifiableList(allDrawables);
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

    public void notifyTimeObservers() {
        for (TimeObserver observer : timeObservers) {
            observer.onTimeUpdate(timeCycle);
        }
    }

    public void night() {
        for (TimeObserver observer : timeObservers) {
            observer.onNightStart(timeCycle);
        }
    }

    public void day() {
        for (TimeObserver observer : timeObservers) {
            observer.onDayStart(timeCycle);
        }
    }

    private List<Updatable> getUpdatables() {
        List<Updatable> updatables = new ArrayList<>();
        updatables.addAll(entities);
        updatables.addAll(structures);
        return updatables;
    }
    public void update(float deltaTime) {
        timeCycle.update(deltaTime);

        List<Updatable> updateTheseEntities = getUpdatables();
        Updatable spotlightedUpdateable;
        while (!updateTheseEntities.isEmpty()) {
            spotlightedUpdateable = updateTheseEntities.removeFirst();
            spotlightedUpdateable.update(deltaTime);
        }
        // Update fog after movement
        fogSystem.updateFog(entities);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity e) {
        entities.remove(e);
    }

    @Override
    public void onEntityDeath(Entity e) {
        removeEntity(e);
    }

    public void addStructure(Structure structure) {
        structures.add(structure);
    }

    public void removeStructure(Structure s) {
        structures.remove(s);
    }

    @Override
    public void onStructureDeath(Structure s) {
        removeStructure(s);
    }
}
