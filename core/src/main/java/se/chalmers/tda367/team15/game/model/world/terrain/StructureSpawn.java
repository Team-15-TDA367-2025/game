package se.chalmers.tda367.team15.game.model.world.terrain;

import java.util.Map;

import com.badlogic.gdx.math.GridPoint2;

/**
 * Represents a structure to be spawned in the world.
 */
public class StructureSpawn {
    private final GridPoint2 position;
    private final String type;
    private final Map<String, Object> properties;

    public StructureSpawn(GridPoint2 position, String type) {
        this(position, type, null);
    }

    public StructureSpawn(GridPoint2 position, String type, Map<String, Object> properties) {
        this.position = position;
        this.type = type;
        this.properties = properties;
    }

    public GridPoint2 getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}

