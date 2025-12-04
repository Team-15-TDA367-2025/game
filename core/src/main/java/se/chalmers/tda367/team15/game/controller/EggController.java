package se.chalmers.tda367.team15.game.controller;

import se.chalmers.tda367.team15.game.model.structure.Colony;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;

/**
 * Controller for handling egg purchase logic.
 * Bridges the view layer and model layer following MVC pattern.
 */
public class EggController {
    private final Colony colony;

    public EggController(Colony colony) {
        this.colony = colony;
    }

    /**
     * Attempts to purchase an egg of the specified type.
     *
     * @param typeId the ID of the egg type to purchase
     * @return true if the purchase was successful, false otherwise
     */
    public boolean purchaseEgg(String typeId) {
        AntTypeRegistry registry = AntTypeRegistry.getInstance();
        AntType type = registry.get(typeId);
        
        if (type == null) {
            return false;
        }

        return colony.purchaseEgg(type);
    }

    /**
     * Gets the colony associated with this controller.
     *
     * @return the colony
     */
    public Colony getColony() {
        return colony;
    }
}
