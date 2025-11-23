package se.chalmers.tda367.team15.game.model;


public interface AntBehaviour {
    /**
     *
     * @param currentX the x position of the ant
     * @param currentY the y position of the ant
     * @param pheromoneHandler the PheromoneHandler of the world.
     * @return x and y position of current objective as an int[] {x,y}
     */
    float[] positionOfObjective(float currentX, float currentY, float sightRange, PheromoneHandler pheromoneHandler);
}
