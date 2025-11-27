package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.Termite.Termite;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.structure.Colony;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;

public class GameModel {
    private final GameWorld world;
    private final PheromoneSystem pheromoneSystem;

    public GameModel(int mapWidth, int mapHeight, float tileSize) {
        this.world = new GameWorld(mapWidth, mapHeight, tileSize);
        GridPoint2 colonyPosition = new GridPoint2(0, 0);
        this.world.addStructure(new Colony(colonyPosition));
        this.pheromoneSystem = new PheromoneSystem(colonyPosition);
    }

    // --- FACADE METHODS (Actions) ---

    public void spawnAnt(Vector2 position) {
        Ant ant = new Ant(position, pheromoneSystem,world);
        world.addEntity(ant);
    }

    public void spawnTermite(Vector2 position) {
        Termite termite = new Termite(position, world);
        world.addEntity(termite);
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
}
