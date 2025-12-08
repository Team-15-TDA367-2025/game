package se.chalmers.tda367.team15.game.controller;

import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;
import se.chalmers.tda367.team15.game.model.structure.Colony;

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

    public Colony getColony() {
        return colony;
    }
}
