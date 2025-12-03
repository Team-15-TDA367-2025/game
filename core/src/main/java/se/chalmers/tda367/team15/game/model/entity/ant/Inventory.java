package se.chalmers.tda367.team15.game.model.entity.ant;

import java.util.HashMap;
import java.util.Map;

import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;

public class Inventory {
    private final Map<ResourceType, Integer> resources;
    private final int capacity;

    public Inventory(int capacity) {
        this.capacity = capacity;
        this.resources = new HashMap<>();
    }

    public boolean addResource(ResourceType type, int amount) {
        if (getTotalAmount() + amount > capacity) {
            return false;
        }
        resources.put(type, resources.getOrDefault(type, 0) + amount);
        return true;
    }

    public int getAmount(ResourceType type) {
        return resources.getOrDefault(type, 0);
    }

    public int getTotalAmount() {
        return resources.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean isFull() {
        return getTotalAmount() >= capacity;
    }

    public int getRemainingCapacity() {
        return capacity - getTotalAmount();
    }

    public boolean isEmpty() {
        return resources.isEmpty();
    }

    public void clear() {
        resources.clear();
    }
}