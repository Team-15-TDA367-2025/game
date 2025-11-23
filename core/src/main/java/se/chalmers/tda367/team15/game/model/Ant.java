package se.chalmers.tda367.team15.game.model;


import java.util.ArrayList;
import java.util.List;

public class Ant {
    float xPos;
    float yPos;
    float tileSize;
    float movementDistance = 0.05f;
    float sightRange = 15;
    AntBehaviour antBehaviour = new seekFoodBehaviour();
    Ant(float x,float y,float sizeOfTiles){
        xPos=x;
        yPos=y;
        tileSize=sizeOfTiles;
    }

    Ant(float x,float y) {
         xPos=x;
         yPos=y;
    }

    public void update(PheromoneHandler pheromoneHandler) {
        // find objective
        float[] objectivePos = antBehaviour.positionOfObjective(xPos,yPos,sightRange,pheromoneHandler);

        float objectiveX=objectivePos[0];
        float objectiveY=objectivePos[1];
        float distanceToObjective= Map.calculateDistance(xPos,yPos,objectiveX,objectiveY);

        // Move to objective
        if(distanceToObjective <= movementDistance){
            xPos = objectiveX;
            yPos = objectiveY;
        }
        else{
            // For each axis: find the (x or y as appropriate) component of the vector that points to objective
            // but has length 1. And then multiply by distance traveled. This is the new x / y pos.
             xPos = movementDistance * ((objectiveX-xPos) / distanceToObjective);
             yPos = movementDistance * ((objectiveY-yPos) / distanceToObjective);
        }

    }
}
