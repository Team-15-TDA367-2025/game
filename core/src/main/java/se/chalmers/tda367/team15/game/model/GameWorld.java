package se.chalmers.tda367.team15.game.model;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.tda367.team15.game.model.entity.Entity;

public class GameWorld {
    private List<Entity> entities;
    private final FogSystem fogSystem;
    private final FogOfWar fogOfWar;

    public GameWorld(int mapWidth, int mapHeight, float tileSize) {
        fogOfWar = new FogOfWar(mapWidth, mapHeight, tileSize);
        fogSystem = new FogSystem(fogOfWar);
        this.entities = new ArrayList<>();
    }

    public List<Entity> getEntities() {
        return new ArrayList<>(entities);
    }

    public List<Drawable> getDrawables() {
        // We just have entities right now, we might need to change this in the future.
        return new ArrayList<>(entities);
    }

    public FogOfWar getFog() {
        return fogOfWar;
    }

    public void update(float delta) {
        for (Entity e : entities) {
            e.update(delta);
        }
        // Update fog after movement
        fogSystem.updateFog(entities);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }
}
