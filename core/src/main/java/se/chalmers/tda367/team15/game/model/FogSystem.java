package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.Vector2;
import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.interfaces.VisionProvider;
import se.chalmers.tda367.team15.game.model.world.WorldMap;

import java.util.List;

public class FogSystem {
    private final FogOfWar fogOfWar;
    private final WorldMap worldMap;

    public FogSystem(FogOfWar fogOfWar, WorldMap worldMap) {
        this.fogOfWar = fogOfWar;
        this.worldMap = worldMap;
    }

    public void updateFog(List<Entity> entities) {
        for (Entity e : entities) {
            if (e instanceof VisionProvider vp) {
                Vector2 position = e.getPosition();
                fogOfWar.reveal(worldMap.worldToTile(position), vp.getVisionRadius());
            }
        }
    }
}
