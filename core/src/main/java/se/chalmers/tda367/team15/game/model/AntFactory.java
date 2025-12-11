package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.structure.Colony;

public class AntFactory {
    private final PheromoneSystem pheromoneSystem;
    private final Colony colony;
    private final GameWorld world;
    private final SimulationHandler simulationHandler;

    public AntFactory(PheromoneSystem pheromoneSystem, Colony colony, GameWorld world,SimulationHandler simulationHandler) {
        this.pheromoneSystem = pheromoneSystem;
        this.colony = colony;
        this.world = world;
        this.simulationHandler = simulationHandler;
    }

    public Ant createAnt(AntType type) {
        Vector2 position = colony.getPosition();
        Ant a = new Ant(position, pheromoneSystem, type, world);
        simulationHandler.addUpdateObserver(a);
        return a;
    }
}
