package se.chalmers.tda367.team15.game.controller;

import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;
import se.chalmers.tda367.team15.game.model.interfaces.EggPurchaseProvider;

public class EggController {
    private final AntTypeRegistry antTypeRegistry;
    private final EggPurchaseProvider eggPurchaseProvider;

    public EggController(AntTypeRegistry antTypeRegistry, EggPurchaseProvider eggPurchaseProvider) {
        this.antTypeRegistry = antTypeRegistry;
        this.eggPurchaseProvider = eggPurchaseProvider;
    }

    /**
     * Attempts to purchase an egg of the specified type.
     *
     * @param typeId the ID of the egg type to purchase
     * @return true if the purchase was successful, false otherwise
     */
    public boolean purchaseEgg(String typeId) {
        AntType type = antTypeRegistry.get(typeId);

        if (type == null) {
            return false;
        }

        return eggPurchaseProvider.purchaseEgg(type);
    }
}
