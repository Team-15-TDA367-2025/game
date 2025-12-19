package se.chalmers.tda367.team15.game.model.interfaces.providers;

import se.chalmers.tda367.team15.game.model.entity.ant.AntType;

public interface EggPurchaseProvider {
    boolean purchaseEgg(AntType type);
}
