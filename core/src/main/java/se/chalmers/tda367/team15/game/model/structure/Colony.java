package se.chalmers.tda367.team15.game.model.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.Inventory;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;

public class Colony extends Structure {
    private List<Ant> ants;
    private Map<ResourceType, Integer> storage;

    public Colony(GridPoint2 position) {
        super(position, "AntColony", 5);
        this.ants = new ArrayList<>();
        this.storage = new HashMap<>();
    }

    public void addAnt(Ant ant) {
        ants.add(ant);
    }

    public void removeAnt(Ant ant) {
        ants.remove(ant);
    }

    public boolean depositResources(Inventory inventory) {
        boolean deposited = false;

        for (ResourceType type : ResourceType.values()) {
            int amount = inventory.getAmount(type);
            if (amount > 0) {
                storage.put(type, storage.getOrDefault(type, 0) + amount);
                deposited = true;
            }
        }
        return deposited;
    }

    @Override
    public void update(float deltaTime) {
        for (Ant ant : ants) {
            ant.update(deltaTime);
        }
    }

    @Override
    public Collection<Entity> getSubEntities() {
        return Collections.unmodifiableCollection(ants);
    }
}
