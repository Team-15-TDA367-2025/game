package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.structure.Colony;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.world.TerrainGenerator;
import se.chalmers.tda367.team15.game.model.world.WorldMap;

public class GameModel {
    private final GameWorld world;
    private final PheromoneSystem pheromoneSystem;
    private final PheromoneGridConverter pheromoneGridConverter;

    public GameModel(TimeCycle timeCycle, int mapWidth, int mapHeight, float tileSize, int pheromonesPerTile, TerrainGenerator generator) {
        this.world = new GameWorld(timeCycle, mapWidth, mapHeight, tileSize, generator);
        this.pheromoneGridConverter = new PheromoneGridConverter(tileSize, pheromonesPerTile);
        GridPoint2 colonyPosition = new GridPoint2(0, 0);
        this.world.addStructure(new Colony(colonyPosition));
        this.pheromoneSystem = new PheromoneSystem(colonyPosition, pheromoneGridConverter);
    }

    public PheromoneGridConverter getPheromoneGridConverter() {
        return pheromoneGridConverter;
    }

    // --- FACADE METHODS (Actions) ---

    public void spawnAnt(Vector2 position) {
        Ant ant = new Ant(position, pheromoneSystem);
        world.addEntity(ant);
    }

    public void update(float deltaTime) {
        world.update(deltaTime);
    }

    // --- GETTERS (For View) ---

    public Iterable<Drawable> getDrawables() {
        return world.getDrawables();
    }

    public FogOfWar getFog() {
        return world.getFog();
    }

    public PheromoneSystem getPheromoneSystem() {
        return pheromoneSystem;
    }

    public WorldMap getWorldMap() {
        return world.getWorldMap();
    }
}
