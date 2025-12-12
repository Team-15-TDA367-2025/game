package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;

public class AntFactory {
    private final PheromoneSystem pheromoneSystem;
    private final Home home;
    private final GameWorld world;

    public AntFactory(PheromoneSystem pheromoneSystem, Home home, GameWorld world) {
        this.pheromoneSystem = pheromoneSystem;
        this.home = home;
        this.world = world;
    }

    public Ant createAnt(AntType type) {
        Vector2 position = home.getPosition();
        return new Ant(position, pheromoneSystem, type, world);
    }
}
