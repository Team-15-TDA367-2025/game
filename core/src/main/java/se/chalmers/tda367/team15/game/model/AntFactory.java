package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.managers.StructureManager;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.world.MapProvider;

import java.util.HashMap;

public class AntFactory {
    private final PheromoneSystem pheromoneSystem;
    private final MapProvider map;
    private final EntityQuery entityQuery;
    private final DestructionListener destructionListener;
    private final StructureManager structureManager;
    private final HashMap<AttackCategory, Integer> targetPriority;

    public AntFactory(PheromoneSystem pheromoneSystem, MapProvider map, EntityQuery entityQuery, DestructionListener destructionListener, StructureManager structureManager, HashMap<AttackCategory, Integer> targetPriority) {
        this.pheromoneSystem = pheromoneSystem;
        this.map = map;
        this.entityQuery = entityQuery;
        this.destructionListener = destructionListener;
        this.structureManager = structureManager;
        this.targetPriority = targetPriority;
    }

    public Ant createAnt(Home home, AntType type) {
        Vector2 position = home.getPosition();
        return new Ant(position, pheromoneSystem, type, map, home, entityQuery,structureManager,targetPriority,destructionListener);
    }
}
