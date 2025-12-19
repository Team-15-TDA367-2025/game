package se.chalmers.tda367.team15.game.model.managers;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.tda367.team15.game.model.interfaces.observers.SimulationObserver;
import se.chalmers.tda367.team15.game.model.interfaces.providers.SimulationProvider;

public class SimulationManager implements SimulationProvider {
    private static final int baseTickPerSecond = 100; // Do not set lower than 50
    private static final double inGameTimePerTickMs = 1000.0 / baseTickPerSecond;
    // TODO : Speed Factor instead
    private int iRLTicksPerSecond = baseTickPerSecond;
    private double accumulator = 0;
    private long previous = System.currentTimeMillis();

    private long now = System.currentTimeMillis();
    private boolean paused = false;

    private final List<SimulationObserver> simulationObservers = new ArrayList<>();

    public SimulationManager() {
    }

    public void addUpdateObserver(SimulationObserver u) {
        simulationObservers.add(u);
    }

    public void setTimeFast() {
        setTicksPerSecond(baseTickPerSecond * 3);
    }

    public void setTimePaused() {
        setTicksPerSecond(0);
    }

    public void setTimeNormal() {
        setTicksPerSecond(baseTickPerSecond);
    }

    private void setTicksPerSecond(int ticksPerSecond) {
        if (ticksPerSecond < 0) {
            throw new IllegalArgumentException("ticks per second can't be negative");
        }
        this.iRLTicksPerSecond = ticksPerSecond;

        // We want to prevent "catch up" when exiting pause
        boolean oldPause = paused;
        paused = iRLTicksPerSecond == 0;
        if (oldPause && !paused) {
            accumulator = 0;
            previous = System.currentTimeMillis();
        }

    }

    public void handleSimulation() {
        if (iRLTicksPerSecond != 0) {
            long mSPerTick = mSPerTick();
            now = System.currentTimeMillis();
            long difference = now - previous;
            previous = now;

            accumulator += difference;
            while (accumulator >= mSPerTick) {
                float inGameTimeDifference = (float) inGameTimePerTickMs / 1000f;
                List<SimulationObserver> updateThese = new ArrayList<>(simulationObservers);
                for (SimulationObserver u : updateThese) {
                    u.update(inGameTimeDifference);
                }

                accumulator -= mSPerTick;
            }
        }
    }

    private long mSPerTick() {
        if (iRLTicksPerSecond == 0) {
            throw new IllegalStateException("infinite time for each frame is undefined so the game shouldn't run");
        }
        return 1000 / iRLTicksPerSecond;
    }

    public void removeUpdateObserver(SimulationObserver u) {
        simulationObservers.remove(u);
    }
}
