package se.chalmers.tda367.team15.game.model.pheromones;

import com.badlogic.gdx.math.GridPoint2;

public class Pheromone {
    private final GridPoint2 position;
    private final PheromoneType type;
    private final int distance;

    public Pheromone(GridPoint2 position, PheromoneType type, int distance) {
        this.position = position;
        this.type = type;
        this.distance = distance;
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
}

