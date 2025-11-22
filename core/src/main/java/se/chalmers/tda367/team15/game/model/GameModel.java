package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.Ant;

public class GameModel {
    private final GameWorld world;

    public GameModel() {
        this.world = new GameWorld();
    }

    // --- FACADE METHODS (Actions) ---

    public void spawnAnt(Vector2 position) {
        Ant ant = new Ant(position);
        world.addEntity(ant);
    }

    public void update(float deltaTime) {
        world.update(deltaTime);
    }

    // --- GETTERS (For Rendering) ---
    public Iterable<IDrawable> getDrawables() {
        return world.getDrawables();
    }
}

