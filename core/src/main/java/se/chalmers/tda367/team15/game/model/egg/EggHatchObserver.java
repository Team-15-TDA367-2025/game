package se.chalmers.tda367.team15.game.model.egg;

import se.chalmers.tda367.team15.game.model.AntFactory;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;

public interface EggHatchObserver {
    void onEggHatch(AntFactory antFactory, AntType type);
}
