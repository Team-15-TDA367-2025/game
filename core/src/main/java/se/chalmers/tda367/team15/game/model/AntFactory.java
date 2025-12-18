package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.interfaces.StructureProvider;
import se.chalmers.tda367.team15.game.model.managers.PheromoneManager;
import se.chalmers.tda367.team15.game.model.world.MapProvider;

import java.util.HashMap;

public class AntFactory {
    private final PheromoneManager pheromoneManager;
    private final MapProvider map;
    private final EntityQuery entityQuery;
    private final DestructionListener destructionListener;
    private final StructureProvider structureProvider;
    private final HashMap<AttackCategory, Integer> targetPriority;

    public AntFactory(PheromoneManager pheromoneManager, MapProvider map, EntityQuery entityQuery, DestructionListener destructionListener, StructureProvider structureProvider, HashMap<AttackCategory, Integer> targetPriority) {
        this.pheromoneManager = pheromoneManager;
        this.map = map;
        this.entityQuery = entityQuery;
        this.destructionListener = destructionListener;
        this.structureProvider = structureProvider;
        this.targetPriority = targetPriority;
    }

    public Ant createAnt(Home home, AntType type) {
        Vector2 position = home.getPosition();
        return new Ant(position, pheromoneManager, type, map, home, entityQuery,structureProvider,targetPriority,destructionListener);
    }
}
