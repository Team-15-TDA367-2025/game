package se.chalmers.tda367.team15.game.model;

public class TimeCycle {
    private long combatPhaseMs = 5 * 1000;
    private long standardPhaseMs = 1 * 1000;
    private boolean combatPhaseCurrent;

    private long timeUntilNextPhaseMs = 0;

    public TimeCycle() {
            combatPhaseCurrent = false;
            timeUntilNextPhaseMs = standardPhaseMs ;
    }

    public void update(float deltaTime) {
        long msDeltaTime = Math.round(deltaTime * 1000f);

        if ((timeUntilNextPhaseMs - msDeltaTime) <= 0) {
            nextPhase();
        } else {
            timeUntilNextPhaseMs -= msDeltaTime;
        }
        System.out.println(timeUntilNextPhaseMs / 1000);

    }


    public long getTimeUntilNextPhaseMS() {
        return timeUntilNextPhaseMs;
    }

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
