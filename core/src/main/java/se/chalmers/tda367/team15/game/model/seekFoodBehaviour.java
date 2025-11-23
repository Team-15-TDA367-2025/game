package se.chalmers.tda367.team15.game.model;

import java.util.ArrayList;

public class seekFoodBehaviour implements AntBehaviour{
    @Override
    public float[] positionOfObjective(float currentX, float currentY, float sightRange,PheromoneHandler pheromoneHandler) {
        ArrayList<Pheromone> pheromoneList = pheromoneHandler.getPheromoneList();
        Pheromone currentObjective= pheromoneList.getFirst();
        // pheromones within sight
        ArrayList<Pheromone> withinSightPheromones= withinSightPheromones(currentX,currentY,sightRange,pheromoneList);

        // default behaviour stand still
        float[] returnThis ={currentX,currentY};

        // if there are no pheromones within sight we do not modify result.
        if(!withinSightPheromones.isEmpty()) {
            // Now we analyze pheromones to determine action.
            for (Pheromone pheromone: withinSightPheromones) {
                // ant should only pursue the pheromone with the lowest age / "strongest scent"
                if (pheromone.getCreationDate() < currentObjective.getCreationDate()) {
                    currentObjective = pheromone;
                }
            }
            int objectiveX = currentObjective.getPositionX();
            int objectiveY = currentObjective.getPositionY();
            returnThis[0]=Map.xOrYPosOfCenterOfTile(objectiveX);
            returnThis[1] =Map.xOrYPosOfCenterOfTile(objectiveY);
        }

        return returnThis;
    }

    ArrayList<Pheromone> withinSightPheromones(float currentX, float currentY, float sightRange,ArrayList<Pheromone> pheromoneList) {
        ArrayList<Pheromone> withinSightPheromones = new ArrayList<>();
        for (Pheromone pheromone: pheromoneList) {
            float pheromoneX = Map.xOrYPosOfCenterOfTile(pheromone.getPositionX());
            float pheromoneY = Map.xOrYPosOfCenterOfTile(pheromone.getPositionY());
            if (Map.calculateDistance(currentX, currentY, pheromoneX, pheromoneY) <= sightRange) {
                withinSightPheromones.add(pheromone);
            }
        }
        return withinSightPheromones;
    }
    }

