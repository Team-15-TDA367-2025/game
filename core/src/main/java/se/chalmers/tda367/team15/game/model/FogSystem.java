package se.chalmers.tda367.team15.game.model;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.interfaces.Updatable;
import se.chalmers.tda367.team15.game.model.interfaces.VisionProvider;
import se.chalmers.tda367.team15.game.model.world.WorldMap;

public class FogSystem implements Updatable {
    private final FogOfWar fogOfWar;
    private final WorldMap worldMap;

    private final GameWorld gameWorld;

    public FogSystem(GameWorld gameWorld,SimulationHandler simulationHandler,FogOfWar fogOfWar, WorldMap worldMap) {
        this.fogOfWar = fogOfWar;
        this.worldMap = worldMap;
        this.gameWorld=gameWorld;
        simulationHandler.addUpdateObserver(this);
    }
    @Override
    public void update(float deltaTime) {
        List<Entity> entities = new ArrayList<>(gameWorld.getEntities());
        for (Entity e : entities) {
            if (e instanceof VisionProvider vp) {
                Vector2 position = e.getPosition();
                fogOfWar.reveal(worldMap.worldToTile(position), vp.getVisionRadius());
            }
        }
    }
}
