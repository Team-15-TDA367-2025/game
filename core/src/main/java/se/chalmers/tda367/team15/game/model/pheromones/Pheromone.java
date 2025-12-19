package se.chalmers.tda367.team15.game.model.pheromones;

import com.badlogic.gdx.math.GridPoint2;

public class Pheromone {
    private final GridPoint2 position;
    private final PheromoneType type;
    private int distance;
    private int soldierCount;

    public Pheromone(GridPoint2 position, PheromoneType type, int distance) {
        this.position = position;
        this.type = type;
        this.distance = distance;
        this.soldierCount = 0;
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

    public int getSoldierCount() {
        return soldierCount;
    }

    public void incrementSoldierCount() {
        soldierCount++;
    }

    public void decrementSoldierCount() {
        if (soldierCount > 0) {
            soldierCount--;
        }
    }
}
