package se.chalmers.tda367.team15.game.model;

public class SimulationHandler {
    private final GameWorld gameWorld;
    private final TimeCycle timeCycle;

    private final int baseTickPerSecond = 20;
    private final double inGameTimePerTick = 1000.0 / baseTickPerSecond;


    private int iRLTicksPerSecond = baseTickPerSecond;
    private double accumulator = 0;
    private long previous = System.currentTimeMillis();

    private long now = System.currentTimeMillis();
    private boolean paused=false;

    SimulationHandler(GameWorld gameWorld, TimeCycle timeCycle) {
        this.gameWorld = gameWorld;
        this.timeCycle = timeCycle;
    }

    public void setTicksPerSecond(int ticksPerSecond) {
        if(ticksPerSecond < 0) {
            throw new IllegalArgumentException("ticks per second can't be negative");
        }
        this.iRLTicksPerSecond = ticksPerSecond;
    }

    public TimeCycle getTimeCycle() {
        return timeCycle;
    }

    public void handleSimulation() {
       boolean oldPause = paused;
       paused= iRLTicksPerSecond == 0;
        // Only update this.now if we aren't exiting out of pause, use old value instead.
        // This prevents "catch up" when exiting pause.
        if(!(oldPause && !paused)) {
            now=System.currentTimeMillis();
        }

        if(!(iRLTicksPerSecond==0)) {
            long mSPerTick = mSPerTick();
            long difference = now - previous;
            previous = now;

            accumulator += difference;
            while (accumulator >= mSPerTick) {
                float inGameTimeDifference = (float) inGameTimePerTick / 1000f;
                timeCycle.tick(); // time cycle needs to update first
                gameWorld.update(inGameTimeDifference);
                accumulator -= mSPerTick;
            }
        }
    }
    private long mSPerTick() {
        if(iRLTicksPerSecond==0) {
            throw new IllegalStateException("infinite time for each frame is undefined so the game shouldn't run");
        }
        return 1000/iRLTicksPerSecond;
    }

}
