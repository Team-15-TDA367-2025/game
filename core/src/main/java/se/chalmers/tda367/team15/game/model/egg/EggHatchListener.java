package se.chalmers.tda367.team15.game.model.egg;

import se.chalmers.tda367.team15.game.model.entity.ant.AntType;

/**
 * Observer interface for egg hatching events.
 * Implemented by classes that need to react when an egg hatches.
 */
public interface EggHatchListener {
    /**
     * Called when an egg hatches.
     *
     * @param type the type of egg that hatched
     */
    void onEggHatch(AntType type);
}
