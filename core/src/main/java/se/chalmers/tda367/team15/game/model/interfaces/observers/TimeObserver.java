package se.chalmers.tda367.team15.game.model.interfaces.observers;

/**
 * All time observers need to be added to the TimeCycle in order to receive
 * updates
 */
public interface TimeObserver {
    default void onDayStart() {
    }

    default void onNightStart() {
    }

    default void onMinute() {

    }
}
