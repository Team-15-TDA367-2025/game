package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.termite.Termite;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.StructureProvider;

public class EnemyFactory {
    private final DestructionListener destructionListener;
    private final EntityQuery entityQuery;
    private final StructureProvider structureProvider;

    public EnemyFactory(EntityQuery entityQuery, StructureProvider structureProvider, DestructionListener destructionListener) {
        this.entityQuery = entityQuery;
        this.structureProvider = structureProvider;
        this.destructionListener = destructionListener;
    }

    public Termite createTermite(Vector2 pos) {
        return new Termite(pos, entityQuery, structureProvider, destructionListener);
    }
}
