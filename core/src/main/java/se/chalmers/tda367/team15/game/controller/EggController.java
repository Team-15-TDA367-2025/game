package se.chalmers.tda367.team15.game.controller;

import se.chalmers.tda367.team15.game.model.GameModel;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;

public class EggController {
    private final GameModel model;

    public EggController(GameModel model) {
        this.model = model;
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

        return model.getColonyUsageProvider().purchaseEgg(type);
    }
}
