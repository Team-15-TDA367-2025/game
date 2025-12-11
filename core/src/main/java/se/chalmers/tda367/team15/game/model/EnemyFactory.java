package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.Termite.Termite;

public class EnemyFactory {
    private GameWorld world;
    private SimulationHandler simulationHandler;

    public EnemyFactory(GameWorld world, SimulationHandler simulationHandler) {
        this.world = world;
        this.simulationHandler=simulationHandler;
    }

    public Termite createTermite(Vector2 pos) {
        Termite t = new Termite(pos, world);
        simulationHandler.addUpdateObserver(t);
        return t;
    }
}
