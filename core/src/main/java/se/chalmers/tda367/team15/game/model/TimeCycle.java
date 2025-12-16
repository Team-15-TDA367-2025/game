package se.chalmers.tda367.team15.game.model;

import se.chalmers.tda367.team15.game.model.interfaces.TimeObserver;

import java.util.ArrayList;
import java.util.List;

public class TimeCycle {
    private int minutes;
    private int ticksPerMinute;
    private final List<TimeObserver> timeObservers = new ArrayList<>();

    private int tickCountDown;

    public record GameTime(int totalDays, int currentHour, int currentMinute, int ticks) {
    }

    public TimeCycle(int ticksPerMinute) {
        this.ticksPerMinute = ticksPerMinute;
        tickCountDown = ticksPerMinute;
        this.minutes = 0;
    }

    public void tick() {
        tickCountDown--;
        if (tickCountDown == 0) {
            tickCountDown = ticksPerMinute;
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

    public int getTotalMinutes() {
        return minutes;
    }

    public int getHour() {
        return (getTotalMinutes() / 60) % 24;
    }

    public int getMinute() {
        return getTotalMinutes() % 60;
    }

    public void setTicksPerMinute(int ticksPerMinute) {
        this.ticksPerMinute = ticksPerMinute;
    }

    public int getTicksPerMinute() {
        return ticksPerMinute;
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
