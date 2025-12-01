package se.chalmers.tda367.team15.game.model.interfaces;
import se.chalmers.tda367.team15.game.model.TimeCycle;

public interface TimeObserver {
    default void onTimeUpdate(TimeCycle timeCycle) {};


    default void onDayStart(TimeCycle timeCycle) {}


    default void onNightStart(TimeCycle timeCycle) {}
}
