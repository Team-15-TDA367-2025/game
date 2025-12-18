package se.chalmers.tda367.team15.game.model;

import se.chalmers.tda367.team15.game.model.interfaces.TimeCycleDataProvider;
import se.chalmers.tda367.team15.game.model.interfaces.TimeObserver;
import se.chalmers.tda367.team15.game.model.interfaces.SimulationObserver;

import java.util.ArrayList;
import java.util.List;

public class TimeCycle implements SimulationObserver, TimeCycleDataProvider {
    private int minutes;
    private final float timePerMinute;
    private final List<TimeObserver> timeObservers = new ArrayList<>();

    private float timeSinceLastMinute = 0;

    public record GameTime(int totalDays, int currentHour, int currentMinute, int ticks) {
    }

    public TimeCycle(float timePerMinute) {
        this.minutes = 360;
        this.timePerMinute = timePerMinute;
    }

    @Override
    public void update(float deltaTime) {
        timeSinceLastMinute += deltaTime;
        if (timeSinceLastMinute >= timePerMinute) {
            timeSinceLastMinute = 0;
            boolean oldIsDay = getIsDay();
            minutes++;
            boolean newIsDay = getIsDay();

            for (TimeObserver observer : timeObservers) {
                observer.onMinute();

                if (oldIsDay && !newIsDay) {
                    observer.onNightStart();

                }
                if (!oldIsDay && newIsDay) {
                    observer.onDayStart();
                }
            }

        }
    }

    public void addTimeObserver(TimeObserver observer) {
        timeObservers.add(observer);
    }

    public void removeTimeObserver(TimeObserver observer) {
        timeObservers.remove(observer);
    }

    public int getTotalMinutes() { // TODO: Unnecessary function? just take minutes?
        return minutes;
    }

    public int getHour() {
        return (getTotalMinutes() / 60) % 24;
    }

    public int getMinute() {
        return getTotalMinutes() % 60;
    }

    public GameTime getGameTime() {
        return new GameTime((getTotalMinutes() / (24 * 60)) + 1, getHour(), getMinute(), minutes);
    }

    public boolean getIsDay() {
        int h = getHour();
        return h >= 6 && h < 22;
    }

    public int getTotalDays() {
        return getTotalMinutes() / (60 * 24);
    }
}
