package se.chalmers.tda367.team15.game.model.egg;

import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;

/**
 * Represents a single egg in development.
 * Stores only serializable state (typeId and ticksRemaining) for future
 * save/load support.
 */
public class Egg {
    private final String typeId;
    private int ticksRemaining; // Number of ticks remaining until hatching (can be negative, meaning the egg is hatched)

    public Egg(String typeId, int ticksRemaining) {
        this.typeId = typeId;
        this.ticksRemaining = ticksRemaining;
    }

    public String getTypeId() {
        return typeId;
    }

    public int getTicksRemaining() {
        return ticksRemaining;
    }

    public void tick() {
        ticksRemaining--;
    }

    public boolean isHatched() {
        return ticksRemaining <= 0;
    }

    /** @return progress from 0.0 (just laid) to 1.0 (ready to hatch) */
    public float getProgress() {
        AntType type = AntTypeRegistry.getInstance().get(typeId);
        if (type == null) {
            return 0.0f;
        }

        int totalTicks = type.developmentTicks();
        int elapsedTicks = totalTicks - ticksRemaining;
        return Math.min(1.0f, (float) elapsedTicks / totalTicks);
    }

    /** @return the AntType, or null if not found */
    public AntType getType() {
        return AntTypeRegistry.getInstance().get(typeId);
    }
}
