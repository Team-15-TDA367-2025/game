package se.chalmers.tda367.team15.game.model.entity.ant;

import java.util.HashMap;
import java.util.Map;

import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;

public class Inventory {
    private final Map<ResourceType, Integer> resources;
    private final int capacity;

    /**
     * @param capacity The capacity of the inventory. If null, the inventory is "unlimited".
     */
    public Inventory(Integer capacity) {
        this.capacity = capacity != null ? capacity : Integer.MAX_VALUE;
        this.resources = new HashMap<>();
    }

    /** 
     * Adds a resource to the inventory.
     * @param type The type of resource to add.
     * @param amount The amount of resource to add. Can also be negative to remove resources.
     * @return True if the resource was added, false if the amount would exceed the capacity or be negative.
     */
    public boolean addResource(ResourceType type, int amount) {
        int newAmount = getAmount(type) + amount;
        if (newAmount < 0 || newAmount > capacity) {
            return false;
        }
        resources.put(type, newAmount);
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