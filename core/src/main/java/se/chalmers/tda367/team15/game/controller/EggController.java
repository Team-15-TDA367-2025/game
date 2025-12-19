package se.chalmers.tda367.team15.game.controller;

import java.util.Optional;

import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;
import se.chalmers.tda367.team15.game.model.interfaces.providers.EggProvider;
import se.chalmers.tda367.team15.game.view.ui.EggPanelListener;

public class EggController implements EggPanelListener {
    private final AntTypeRegistry antTypeRegistry;
    private final EggProvider eggProvider;

    public EggController(AntTypeRegistry antTypeRegistry, EggProvider eggProvider) {
        this.antTypeRegistry = antTypeRegistry;
        this.eggProvider = eggProvider;
    }

    /**
     * Attempts to purchase an egg of the specified type.
     *
     * @param typeId the ID of the egg type to purchase
     */
    @Override
    public void onPurchaseEgg(String typeId) {
        Optional<AntType> type = antTypeRegistry.get(typeId);

        if (type.isEmpty()) {
            return;
        }

        eggProvider.purchaseEgg(type.orElseThrow());
    }
}
