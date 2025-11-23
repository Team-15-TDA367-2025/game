package se.chalmers.tda367.team15.game.model;

public class Map {
    int worldHeight = 300;
    int worldWidth = 600;
    PheromoneHandler pheromoneHandler;
    Object[][] gridMap = new Object[worldHeight][worldWidth];
    // the size of each tile in the coordinate system. The idea is that the coordinate position
    // of the tile is the upper left corner of the tile.
    float tileSize = 1;

    Map(PheromoneHandler pHandler){
        pheromoneHandler = pHandler;
    }

    static float calculateDistance(float X1,float Y1,float X2, float Y2){
        //pythagorean theorem "distance between two points"
        return (float)
            Math.sqrt(
                Math.pow(X1-X2,2) + Math.pow(Y1-Y2,2)
            );
    }
    // TODO currently if we want to change the tile size we also have to change static method
    // xOrYPosOfCenterOfTile but i am too hungry rn to figure out a better way. This method should be static
    // since i think there will be many instances where things want to find center of tile???
    static float xOrYPosOfCenterOfTile(int tilePos) {
        return tilePos + 0.5f;
    }
}
