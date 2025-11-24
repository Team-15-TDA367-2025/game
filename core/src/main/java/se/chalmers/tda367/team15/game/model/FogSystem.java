package se.chalmers.tda367.team15.game.model;

import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.VisionProvider;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class FogSystem {
    private final FogOfWar fogOfWar;

    public FogSystem(FogOfWar fogOfWar) {
        this.fogOfWar = fogOfWar;
    }

    public void updateFog(List<Entity> entities) {
        int centerX = fogOfWar.getWidth() / 2;
        int centerY = fogOfWar.getHeight() / 2;

        for (Entity e : entities) {
            if (e instanceof VisionProvider vp) {
                Vector2 position = e.getPosition();
                int tileX = centerX + (int) Math.floor(position.x / fogOfWar.getTileSize()); // AI debugging for coordinates
                int tileY = centerY + (int) Math.floor(position.y / fogOfWar.getTileSize());

                fogOfWar.reveal(tileX, tileY, vp.getVisionRadius());
            }
        }
    }
}
