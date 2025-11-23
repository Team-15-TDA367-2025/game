package se.chalmers.tda367.team15.game.model;


/**
 * Pheromone super class, all Pheromones inherit their base behaviour from this one.
 */
public abstract class Pheromone extends Object {
    private final long creationDate = System.currentTimeMillis();
    private final int maxLifeTimeMs = 45 * 1000;
     long getCreationDate(){
         return creationDate;
     }

    public int getMaxLifeTimeMs() {
        return maxLifeTimeMs;
    }
    abstract PheromoneType getPheromoneType();

}
