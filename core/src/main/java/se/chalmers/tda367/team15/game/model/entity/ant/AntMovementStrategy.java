package se.chalmers.tda367.team15.game.model.entity.ant;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.interfaces.MovementStrategy;
import se.chalmers.tda367.team15.game.model.world.WorldMap;

public class AntMovementStrategy implements MovementStrategy {
    private WorldMap worldMap;

    public AntMovementStrategy(WorldMap worldMap) {
        this.worldMap = worldMap;
    }

    @Override
    public boolean canMoveTo(Vector2 position) {
        if (!worldMap.isInBounds(position)) {
            return false;
        }
        return worldMap.getTile(position).getType().isWalkable();
    }
    
}
