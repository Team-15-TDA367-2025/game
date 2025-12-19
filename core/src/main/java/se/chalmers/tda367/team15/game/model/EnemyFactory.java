package se.chalmers.tda367.team15.game.model;

import java.util.HashMap;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.enemy.Termite;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;

public class EnemyFactory {
    private final DestructionListener destructionListener;
    private final EntityQuery entityQuery;
    private final HashMap<AttackCategory, Integer> targetPriority;

    public EnemyFactory(EntityQuery entityQuery, DestructionListener destructionListener,
            HashMap<AttackCategory, Integer> targetPriority) {
        this.entityQuery = entityQuery;
        this.destructionListener = destructionListener;
        this.targetPriority = targetPriority;
    }

    public Termite createTermite(Vector2 pos) {
        return new Termite(pos, entityQuery, targetPriority, destructionListener);

    }
}
