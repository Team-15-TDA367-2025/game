package se.chalmers.tda367.team15.game.model.interfaces;

public interface TimeObserver {
    default void onDayStart() {
    }

    default void onNightStart() {
    }

    default void onMinute() {
        
    }
}
