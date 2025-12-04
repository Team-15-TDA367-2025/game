package se.chalmers.tda367.team15.game.model.egg;

import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;

/**
 * Represents a single egg in development.
 * Stores only serializable state (typeId and ticksRemaining) for future save/load support.
 */
public class Egg {
    private final String typeId; // Stored as ID for easy serialization
    private int ticksRemaining;

    /**
     * Creates a new egg of the specified type.
     *
     * @param typeId          the ID of the ant type
     * @param ticksRemaining the number of ticks remaining until hatching
     */
    public Egg(String typeId, int ticksRemaining) {
        this.typeId = typeId;
        this.ticksRemaining = ticksRemaining;
    }

    /**
     * Gets the type ID of this egg.
     *
     * @return the type ID
     */
    public String getTypeId() {
        return typeId;
    }

    /**
     * Gets the number of ticks remaining until hatching.
     *
     * @return ticks remaining
     */
    public int getTicksRemaining() {
        return ticksRemaining;
    }

    /**
     * Decrements the ticks remaining by one.
     */
    public void tick() {
        if (ticksRemaining > 0) {
            ticksRemaining--;
        }
    }

    /**
     * Checks if the egg is ready to hatch.
     *
     * @return true if ticksRemaining <= 0, false otherwise
     */
    public boolean isHatched() {
        return ticksRemaining <= 0;
    }

    /**
     * Gets the development progress as a percentage (0.0 to 1.0).
     * Requires looking up the ant type to get the total development time.
     *
     * @return progress from 0.0 (just laid) to 1.0 (ready to hatch)
     */
    public float getProgress() {
        AntType type = AntTypeRegistry.getInstance().get(typeId);
        if (type == null) {
            return 0.0f;
        }
        int totalTicks = type.developmentTicks();
        if (totalTicks == 0) {
            return 1.0f;
        }
        int elapsedTicks = totalTicks - ticksRemaining;
        return Math.min(1.0f, (float) elapsedTicks / totalTicks);
    }

    /**
     * Helper method to resolve the ant type at runtime.
     *
     * @return the AntType, or null if not found
     */
    public AntType getType() {
        return AntTypeRegistry.getInstance().get(typeId);
    }
}
