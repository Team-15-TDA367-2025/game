package se.chalmers.tda367.team15.game.model.entity.ant;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.interfaces.MovementStrategy;
import se.chalmers.tda367.team15.game.model.world.MapProvider;

public class AntMovementStrategy implements MovementStrategy {
    private MapProvider map;

    public AntMovementStrategy(MapProvider worldMap) {
        this.map = worldMap;
    }

    @Override
    public boolean canMoveTo(Vector2 position) {
        if (!map.isInBounds(position)) {
            return false;
        }
        return map.getTile(position).getType().isWalkable();
    }
    
}
