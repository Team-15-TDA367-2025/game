package se.chalmers.tda367.team15.game.model.interfaces;

import se.chalmers.tda367.team15.game.model.TimeCycle;

public interface TimeObserver {
    public void onTimeUpdate(TimeCycle timeCycle);

    public void onDayStart(TimeCycle timeCycle);

    public void onNightStart(TimeCycle timeCycle);
}
