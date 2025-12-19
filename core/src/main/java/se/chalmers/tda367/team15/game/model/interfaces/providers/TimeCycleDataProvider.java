package se.chalmers.tda367.team15.game.model.interfaces.providers;

import se.chalmers.tda367.team15.game.model.TimeCycle;

public interface TimeCycleDataProvider {
    boolean getIsDay();
    TimeCycle.GameTime getGameTime();

}
