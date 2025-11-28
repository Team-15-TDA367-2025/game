package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.structure.Colony;
import se.chalmers.tda367.team15.game.model.structure.resource.Resource;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;

public class GameModel {
    private final GameWorld world;
    private final PheromoneSystem pheromoneSystem;
    GridPoint2 colonyPosition = new GridPoint2(0, 0);
    Colony colony = new Colony(colonyPosition);

    public GameModel(TimeCycle timeCycle, int mapWidth, int mapHeight, float tileSize) {
        this.world = new GameWorld(colony, timeCycle, mapWidth, mapHeight, tileSize);
        this.world.addStructure(colony);

        this.world.addResource(new Resource(new GridPoint2(10, 10), "food", 1, ResourceType.FOOD, 5));
        this.world.addResource(new Resource(new GridPoint2(-10, 10), "food", 1, ResourceType.FOOD, 5));
        this.world.addResource(new Resource(new GridPoint2(10, -10), "food", 1, ResourceType.FOOD, 5));
        this.world.addResource(new Resource(new GridPoint2(20, 25), "food", 1, ResourceType.FOOD, 5));
        this.world.addResource(new Resource(new GridPoint2(-20, 10), "food", 1, ResourceType.FOOD, 5));
        this.world.addResource(new Resource(new GridPoint2(10, -20), "food", 1, ResourceType.FOOD, 5));

        this.pheromoneSystem = new PheromoneSystem(colonyPosition);
    }

    public void spawnAnt(Vector2 position) {
        Ant ant = new Ant(position, pheromoneSystem, 5);
        world.addEntity(ant);
    }

    public void update(float deltaTime) {
        world.update(deltaTime);
    }

    public Iterable<Drawable> getDrawables() {
        return world.getDrawables();
    }

    public FogOfWar getFog() {
        return world.getFog();
    }

    public PheromoneSystem getPheromoneSystem() {
        return pheromoneSystem;
    }
}