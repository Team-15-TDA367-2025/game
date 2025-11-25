package se.chalmers.tda367.team15.game.model;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.tda367.team15.game.model.entity.Entity;

public class GameWorld {
    private List<Entity> entities;
    private Colony colony;

    public GameWorld() {
        this.entities = new ArrayList<>();
        this.colony = new Colony(0, 0); 
    }

    public List<Entity> getEntities() {
        return new ArrayList<>(entities);
    }

    public List<Drawable> getDrawables() {
        // We just have entities right now, we might need to change this in the future.
        List<Drawable> drawables = new ArrayList<>();
        drawables.add(colony);
        drawables.addAll(entities);
        return drawables;
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            entity.update(deltaTime);
        }
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }
}
