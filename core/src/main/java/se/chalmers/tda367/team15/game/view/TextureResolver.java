package se.chalmers.tda367.team15.game.view;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.GameObject;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceNode;

/**
 * Resolves textures for GameObjects and string IDs.
 * Uses a data-driven approach for extensibility.
 */
public class TextureResolver {
    private final TextureRegistry registry;
    private final Map<String, String> idMappings = new HashMap<>();

    public TextureResolver(TextureRegistry registry) {
        this.registry = registry;
        initializeMappings();
    }

    /**
     * Register ID-to-texture mappings. Add new mappings here for new types.
     * This is the extension point for modding/new content.
     */
    private void initializeMappings() {
        idMappings.put("worker", "ant");
        idMappings.put("soldier", "ant");
        idMappings.put("scout", "scout");

        idMappings.put("termite", "termite");
        idMappings.put("colony", "colony");

        idMappings.put("water1", "water");
        idMappings.put("sand1", "sand");
        // grass1, grass2, grass3 exist as assets, so they don't need mapping
    }

    /**
     * Register a custom mapping. Allows modders/extensions to add new types.
     */
    public void registerMapping(String typeId, String textureName) {
        idMappings.put(typeId, textureName);
    }

    /**
     * Resolves the texture for a given GameObject, considering its dynamic state.
     */
    public TextureRegion resolve(GameObject object) {
        // State-aware resolution for specific types
        if (object instanceof Ant) {
            return resolveAnt((Ant) object);
        }
        if (object instanceof ResourceNode) {
            return resolveResourceNode((ResourceNode) object);
        }

        // Fallback to type ID
        return resolve(object.getTypeId());
    }

    private TextureRegion resolveAnt(Ant ant) {
        String baseId = ant.getTypeId();
        boolean isCarrying = !ant.getInventory().isEmpty();

        // Try state-specific texture first
        if (isCarrying) {
            String carryingId = mapIdToTextureName(baseId) + "_carrying";
            return registry.get(carryingId);
        }

        return resolve(baseId);
    }

    private TextureRegion resolveResourceNode(ResourceNode node) {
        boolean isDepleted = node.getCurrentAmount() <= 0;

        if (isDepleted) {
            return registry.get("depleted_node");
        }

        return resolve("node");
    }

    /**
     * Resolves the texture for a given View ID or Model Type ID.
     */
    public TextureRegion resolve(String id) {
        String mappedName = mapIdToTextureName(id);

        if (registry.has(mappedName)) {
            return registry.get(mappedName);
        }

        return registry.get("pixel");
    }

    private String mapIdToTextureName(String id) {
        if (id == null) {
            return "pixel";
        }

        // Check registered mappings first
        if (idMappings.containsKey(id)) {
            return idMappings.get(id);
        }

        // Resource node pattern: resource_* -> node
        if (id.startsWith("resource_")) {
            return "node";
        }

        // Default: ID is the texture name
        return id;
    }
}
