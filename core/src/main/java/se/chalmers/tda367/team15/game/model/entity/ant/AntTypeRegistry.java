package se.chalmers.tda367.team15.game.model.entity.ant;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Central registry for ant types.
 * Allows adding new ant types without modifying existing code.
 * Uses singleton pattern for global access.
 */
public class AntTypeRegistry {
    private static AntTypeRegistry instance;
    private final Map<String, AntType> types;

    private AntTypeRegistry() {
        this.types = new HashMap<>();
    }

    public static AntTypeRegistry getInstance() {
        if (instance == null) {
            instance = new AntTypeRegistry();
        }
        return instance;
    }

    /**
     * Registers a new ant type.
     *
     * @param type the ant type to register
     * @throws IllegalArgumentException if an ant type with the same ID already
     *                                  exists
     */
    public void register(AntType type) {
        if (types.containsKey(type.id())) {
            throw new IllegalArgumentException("Ant type with ID '" + type.id() + "' already exists");
        }
        types.put(type.id(), type);
    }

    /** @return the ant type, or null if not found */
    public AntType get(String id) {
        return types.get(id);
    }

    public Collection<AntType> getAll() {
        return Collections.unmodifiableCollection(types.values());
    }

    public boolean contains(String id) {
        return types.containsKey(id);
    }

    // Should be called when restarting the game to prevent duplicate registration
    // errors.
    public void clear() {
        types.clear();
    }
}
