package se.chalmers.tda367.team15.game.model.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.fog.FogOfWar;
import se.chalmers.tda367.team15.game.model.fog.FogProvider;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.observers.FogObserver;
import se.chalmers.tda367.team15.game.model.interfaces.observers.SimulationObserver;
import se.chalmers.tda367.team15.game.model.interfaces.providers.VisionProvider;
import se.chalmers.tda367.team15.game.model.world.MapProvider;

public class FogManager implements FogProvider, SimulationObserver {
    private final FogOfWar fogOfWar;
    private final MapProvider mapProvider;
    private final EntityQuery entityQuery;
    private final List<FogObserver> observers = new CopyOnWriteArrayList<>();
    
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

        if (fogOfWar.isDirty()) {
            fogOfWar.clearDirty();
            notifyDirty();
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

    private void notifyDirty() {
        for (FogObserver observer : observers) {
            observer.onFogDirty();
        }
    }

    public void addObserver(FogObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(FogObserver observer) {
        observers.remove(observer);
    }
}

