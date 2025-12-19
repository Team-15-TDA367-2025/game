package se.chalmers.tda367.team15.game.model.pheromones;

import com.badlogic.gdx.math.GridPoint2;

public class Pheromone {
    private final GridPoint2 position;
    private final PheromoneType type;
    private int distance;
    private int antCount;

    public Pheromone(GridPoint2 position, PheromoneType type, int distance) {
        this.position = position;
        this.type = type;
        this.distance = distance;
        this.antCount = 0;
    }

    public GridPoint2 getPosition() {
        return position;
    }

    public PheromoneType getType() {
        return type;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getAntCount() {
        return antCount;
    }

    public void incrementAnts() {
        antCount++;
    }

    public void decrementAnts() {
        if (antCount > 0) {
            antCount--;
        }
    }
}
