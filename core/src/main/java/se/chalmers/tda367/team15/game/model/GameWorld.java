package se.chalmers.tda367.team15.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.structure.Structure;


public class GameWorld implements EntityDeathObserver{
    private List<Entity> entities; // Floating positions and can move around.
    private List<Structure> structures; // Integer positions and fixed in place.
    private final FogSystem fogSystem;
    private final FogOfWar fogOfWar;
    private DestructionListener destructionListener;

    public GameWorld(int mapWidth, int mapHeight, float tileSize) {
        fogOfWar = new FogOfWar(mapWidth, mapHeight, tileSize);
        fogSystem = new FogSystem(fogOfWar);
        this.entities = new ArrayList<>();
        this.structures = new ArrayList<>();
        destructionListener = DestructionListener.getInstance();
        destructionListener.addEntityDeathObserver(this);

    }

    public List<Structure> getStructures(){
        return Collections.unmodifiableList(new ArrayList<>(structures));
    }

    public List<Entity> getEntities() {
        List<Entity> allEntities = new ArrayList<>(entities);
        for (Structure structure : structures) {
            allEntities.addAll(structure.getSubEntities());
        }
        return  Collections.unmodifiableList(allEntities);
    }

    public Iterable<Drawable> getDrawables() {
        List<Drawable> allDrawables = new ArrayList<>(structures);
        allDrawables.addAll(getEntities());
        return Collections.unmodifiableList(allDrawables);
    }

    public FogOfWar getFog() {
        return fogOfWar;
    }

    public void update(float deltaTime) {

        // Looks complicated, but if we want things in the game world to be able to update the game world
        // we cannot iterate through a list that might be updated.
        ArrayList<Entity> updateTheseEntities = new ArrayList<>(getEntities());
        Entity spotlightedEntity;
        while(!updateTheseEntities.isEmpty()) {
            spotlightedEntity = updateTheseEntities.removeFirst();
            if(getEntities().contains(spotlightedEntity)) {
                spotlightedEntity.update(deltaTime);
            }
        }

        ArrayList<Structure> updateTheseStructures = new ArrayList<>(getStructures());
        Structure spotlightedStructure;
        while(!updateTheseStructures.isEmpty()) {
            spotlightedStructure = updateTheseStructures.removeFirst();
            if(getStructures().contains(spotlightedStructure)) {
                spotlightedStructure.update(deltaTime);
            }
        }

        // Update fog after movement
        fogSystem.updateFog(entities);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }
    public void removeEntity(Entity e){entities.remove(e);}
    @Override
    public void onEntityDeath(Entity e) {removeEntity(e);}
    public void addStructure(Structure structure) {
        structures.add(structure);
    }

}
