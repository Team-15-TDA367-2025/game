package se.chalmers.tda367.team15.game.model;

public interface SimulationProvider {
    void setTimeFast();
    void setTimeNormal();
    void setTimePaused();
    void handleSimulation();
}
