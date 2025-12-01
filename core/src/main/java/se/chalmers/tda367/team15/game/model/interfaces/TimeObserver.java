package se.chalmers.tda367.team15.game.model.interfaces;
import se.chalmers.tda367.team15.game.model.TimeCycle;

public interface TimeObserver {
    void onTimeUpdate(TimeCycle timeCycle);
}
