package se.chalmers.tda367.team15.game.model.interfaces.providers;

public interface SimulationProvider {
    void setTimeFast();

    void setTimeNormal();

    void setTimePaused();

    void handleSimulation();
}
