package se.chalmers.tda367.team15.game.model.fog;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.SimulationHandler;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.Updatable;
import se.chalmers.tda367.team15.game.model.interfaces.VisionProvider;
import se.chalmers.tda367.team15.game.model.world.WorldMap;

public class FogSystem implements FogProvider, Updatable {
    private final FogOfWar fogOfWar;
    private final WorldMap worldMap;

    private final EntityQuery entityQuery;

    public FogSystem(EntityQuery entityQuery, SimulationHandler simulationHandler, WorldMap worldMap) {
        this.worldMap = worldMap;
        this.fogOfWar = new FogOfWar(worldMap);
        this.entityQuery = entityQuery;
        simulationHandler.addUpdateObserver(this);
    }
    @Override
    public void update(float deltaTime) {
        List<VisionProvider> entities = new ArrayList<>(entityQuery.getEntitiesOfType(VisionProvider.class));
        for (VisionProvider vp : entities) {
            Vector2 position = vp.getPosition();
            fogOfWar.reveal(worldMap.worldToTile(position), vp.getVisionRadius());
        }
    }

    @Override
    public GridPoint2 getSize() {
        return fogOfWar.getSize();
    }

    @Override
    public boolean isDiscovered(GridPoint2 pos) {
        return fogOfWar.isDiscovered(pos);
    }
}
