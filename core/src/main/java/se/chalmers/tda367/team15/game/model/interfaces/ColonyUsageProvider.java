package se.chalmers.tda367.team15.game.model.interfaces;

import se.chalmers.tda367.team15.game.model.egg.EggManager;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;

public interface ColonyUsageProvider extends ColonyDataProvider {
    boolean purchaseEgg(AntType type);

    // TODO: fix this
    EggManager getEggManager();
}
