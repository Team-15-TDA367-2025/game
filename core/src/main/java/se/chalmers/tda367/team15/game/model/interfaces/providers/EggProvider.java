package se.chalmers.tda367.team15.game.model.interfaces.providers;

import java.util.Collection;

import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.managers.egg.Egg;

public interface EggProvider {
    boolean purchaseEgg(AntType type);

    Collection<Egg> getEggs();
}
