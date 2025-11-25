package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.structure.Colony;

public class GameModel {
    private final GameWorld world;
    private final PheromoneSystem pheromoneSystem;

    public GameModel() {
        this.world = new GameWorld();
        GridPoint2 colonyPosition = new GridPoint2(0, 0);
        this.world.addStructure(new Colony(colonyPosition));
        this.pheromoneSystem = new PheromoneSystem(colonyPosition);
    }

    // --- FACADE METHODS (Actions) ---

    public void spawnAnt(Vector2 position) {
        Ant ant = new Ant(position);
        world.addEntity(ant);
    }

    public void update(float deltaTime) {
        // Update entities with access to pheromone system
        world.update(deltaTime, pheromoneSystem);
    }

    // --- GETTERS (For View) ---

    public Iterable<Drawable> getDrawables() {
        return world.getDrawables();
    }

    public PheromoneSystem getPheromoneSystem() {
        return pheromoneSystem;
    }
}

