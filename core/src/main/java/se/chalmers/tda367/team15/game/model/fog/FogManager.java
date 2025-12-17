package se.chalmers.tda367.team15.game.model.fog;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.Updatable;
import se.chalmers.tda367.team15.game.model.interfaces.VisionProvider;
import se.chalmers.tda367.team15.game.model.world.MapProvider;

public class FogManager implements FogProvider, Updatable {
    private final FogOfWar fogOfWar;
    private final MapProvider mapProvider;
    private final EntityQuery entityQuery;

    public FogManager(EntityQuery entityQuery, MapProvider mapProvider) {
        this.mapProvider = mapProvider;
        this.entityQuery = entityQuery;
        this.fogOfWar = new FogOfWar(mapProvider);
    }

    @Override
    public void update(float deltaTime) {
        List<VisionProvider> visionProviders = new ArrayList<>(entityQuery.getEntitiesOfType(VisionProvider.class));
        for (VisionProvider visionProvider : visionProviders) {
            Vector2 position = visionProvider.getPosition();
            fogOfWar.reveal(mapProvider.worldToTile(position), visionProvider.getVisionRadius());
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

    @Override
    public boolean[][] getDiscoveredArray() {
        return fogOfWar.getDiscoveredArray();
    }

    @Override
    public boolean isDirty() {
        return fogOfWar.isDirty();
    }

    @Override
    public void clearDirty() {
        fogOfWar.clearDirty();
    }
}
