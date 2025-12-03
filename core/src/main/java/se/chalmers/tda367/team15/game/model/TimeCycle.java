package se.chalmers.tda367.team15.game.model;

public class TimeCycle {
    private long combatPhaseMs = 1000;
    private long standardPhaseMs =1000 ;
    private boolean combatPhaseCurrent;

    private long timeUntilNextPhaseMs = 0;

    /**
     * Responsible for keeping track whether the world is in combat phase or standard phase.
     * use {@code getTimeUntilNextPhaseMs()} and {@code isCombatPhaseCurrent()} to get info on world phase.
     */
    public TimeCycle() {
            combatPhaseCurrent = false;
            timeUntilNextPhaseMs = standardPhaseMs ;
    }

    /**
     * Counts down the timer to next phase.
     * @param deltaTime the realtime difference in seconds from the last frame.
     */
    public void update(float deltaTime) {
        long msDeltaTime = Math.round(deltaTime * 1000f);

        if ((timeUntilNextPhaseMs - msDeltaTime) <= 0) {
            nextPhase();
        } else {
            timeUntilNextPhaseMs -= msDeltaTime;
        }
       System.out.println(timeUntilNextPhaseMs / 1000);

    }

    /**
     *
     * @return {@code long}  time until next phase in milliseconds
     */
    public long getTimeUntilNextPhaseMs() {
        return timeUntilNextPhaseMs;
    }

    /**
     *
     * @return {@code boolean} combatPhaseCurrent
     */
    public boolean isCombatPhaseCurrent(){
        return combatPhaseCurrent;
    }


    private void nextPhase() {
        System.out.println("-----Next Phase!");
        if (combatPhaseCurrent) {
            timeUntilNextPhaseMs = standardPhaseMs;
            combatPhaseCurrent = false;
            GameWorld.getInstance().day();
        } else {
            timeUntilNextPhaseMs = combatPhaseMs;
            combatPhaseCurrent = true;
            GameWorld.getInstance().night();
        }
    }

}
