package se.chalmers.tda367.team15.game.model;

import se.chalmers.tda367.team15.game.model.interfaces.TimeObserver;
import se.chalmers.tda367.team15.game.model.interfaces.Updatable;

import java.util.ArrayList;
import java.util.List;

public class TimeCycle  {
    private int minutes;
    private int ticksPerMinute;
    private final List<TimeObserver> timeObservers = new ArrayList<>();

    public record GameTime(int totalDays, int currentHour, int currentMinute, int ticks) {
    }

    public TimeCycle(int ticksPerMinute) {
        this.ticksPerMinute = ticksPerMinute;
        this.minutes = 0;
    }


    public void tick() {
        if(minutes % ticksPerMinute == 0) {
            boolean oldIsDay = getIsDay();
            minutes++;
            boolean newIsDay = getIsDay();

            if(oldIsDay && !newIsDay) {
                for (TimeObserver observer : timeObservers) {
                    observer.onNightStart(this);
                }
            }
            if(!oldIsDay && newIsDay) {
                for (TimeObserver observer : timeObservers) {
                    observer.onDayStart(this);
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
