package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.Termite.Termite;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.structure.resource.Resource;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;
import se.chalmers.tda367.team15.game.model.world.TerrainGenerator;
import se.chalmers.tda367.team15.game.model.world.WorldMap;

public class GameModel {
    private final GameWorld world;

    public GameModel(TimeCycle timeCycle, int mapWidth, int mapHeight, TerrainGenerator generator) {
        this.world = new GameWorld(timeCycle, mapWidth, mapHeight, generator);

        this.world.addResource(new Resource(new GridPoint2(-10, 10), "food", 1, ResourceType.FOOD, 5));
        this.world.addResource(new Resource(new GridPoint2(10, -10), "food", 1, ResourceType.FOOD, 5));
        this.world.addResource(new Resource(new GridPoint2(20, 25), "food", 1, ResourceType.FOOD, 5));
        this.world.addResource(new Resource(new GridPoint2(-20, 10), "food", 1, ResourceType.FOOD, 5));
        this.world.addResource(new Resource(new GridPoint2(10, -20), "food", 1, ResourceType.FOOD, 5));
    }

    public PheromoneGridConverter getPheromoneGridConverter() {
        return world.getPheromoneSystem().getConverter();
    }

    // --- FACADE METHODS (Actions) ---

    public void spawnAnt(Vector2 position) {
        Ant ant = new Ant(position, world.getPheromoneSystem(), 5);
        world.getColony().addAnt(ant);
    }

    public void spawnTermite(Vector2 position) {
        Termite termite = new Termite(position, world);
        world.addEntity(termite);
    }

    public void update(float deltaTime) {
        world.update(deltaTime);
    }

    public TimeCycle.GameTime getGameTime() {
        return world.getTimeCycle().getGameTime();
    }

    public Iterable<Drawable> getDrawables() {
        return world.getDrawables();
    }

    public FogOfWar getFog() {
        return world.getFog();
    }

    public PheromoneSystem getPheromoneSystem() {
        return world.getPheromoneSystem();
    }

    public WorldMap getWorldMap() {
        return world.getWorldMap();
    }
}