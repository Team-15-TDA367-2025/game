package se.chalmers.tda367.team15.game.model;

public class TimeCycle {
    private int ticks;
    private int ticksPerMinute;

    public TimeCycle(int ticksPerMinute) {
        this.ticksPerMinute = ticksPerMinute;
        this.ticks = 0;
    }

    public void tick() {
        ticks++;
    }

    public int getTotalMinutes() {
        return ticks / ticksPerMinute;
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

    public boolean getIsDay() {
        int h = getHour();
        return h >= 6 && h < 22;
    }
}
