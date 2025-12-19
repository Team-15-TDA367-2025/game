package se.chalmers.tda367.team15.game.model.entity.ant;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Central registry for ant types.
 * Allows adding new ant types without modifying existing code.
 */
public class AntTypeRegistry {
    private final Map<String, AntType> types;

    public AntTypeRegistry() {
        this.types = new HashMap<>();
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

    // TODO - Antigravity: Returns null on missing key - consider Optional<AntType>
    // or throw exception
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
}
