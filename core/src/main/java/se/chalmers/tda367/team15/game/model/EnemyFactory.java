package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.termite.*;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.StructureProvider;

import java.util.HashMap;

public class EnemyFactory {
    private final DestructionListener destructionListener;
    private final EntityQuery entityQuery;
    private final StructureProvider structureProvider;
    private final HashMap<AttackCategory, Integer> targetPriority;
    public EnemyFactory(EntityQuery entityQuery, StructureProvider structureProvider, DestructionListener destructionListener, HashMap<AttackCategory, Integer> targetPriority) {
        this.entityQuery = entityQuery;
        this.structureProvider = structureProvider;
        this.destructionListener = destructionListener;
        this.targetPriority = targetPriority;
    }

    public Termite createTermite(Vector2 pos) {
        return new Termite(pos, entityQuery, structureProvider,targetPriority, destructionListener);

    }
}
