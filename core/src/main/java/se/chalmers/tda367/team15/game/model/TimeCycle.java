package se.chalmers.tda367.team15.game.model;

public class TimeCycle {
    private int time;
    private String dayNight;

    public TimeCycle() {
        this.time = 0;
        this.dayNight = "Day";
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
        if (time % 24 < 12) {
            dayNight = "Day";
        } else {
            dayNight = "Night";
        }
    }

    public String getDayNight() {
        return dayNight;
    }

    public void setDayNight(String dayNight) {
        this.dayNight = dayNight;
    }

    // Plussar på tiden med 1 timme
    public void incrementTime() {
        this.time = (this.time + 1) % 24;
        if (time < 12) {
            dayNight = "Day";
        } else {
            dayNight = "Night";
        }
    }

}
