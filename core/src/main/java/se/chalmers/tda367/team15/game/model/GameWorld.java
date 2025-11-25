package se.chalmers.tda367.team15.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.structure.Structure;

public class GameWorld {
    private List<Entity> entities; // Floating positions and can move around.
    private List<Structure> structures; // Integer positions and fixed in place.

    public GameWorld() {
        this.entities = new ArrayList<>();
        this.structures = new ArrayList<>();
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

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            entity.update(deltaTime);
        }

        for (Structure structure : structures) {
            structure.update(deltaTime);
        }
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void addStructure(Structure structure) {
        structures.add(structure);
    }
}
