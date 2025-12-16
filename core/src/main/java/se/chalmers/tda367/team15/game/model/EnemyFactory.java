package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.termite.Termite;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;

import java.util.HashMap;

public class EnemyFactory {
    private final GameWorld world;
    private final EntityQuery entityQuery;
    private final DestructionListener destructionListener;
    private final HashMap<AttackCategory, Integer> targetPriority;
    public EnemyFactory(GameWorld world,EntityQuery entityQuery, DestructionListener destructionListener, HashMap<AttackCategory, Integer> targetPriority) {
        this.world = world;
        this.destructionListener = destructionListener;
        this.entityQuery=entityQuery;
        this.targetPriority=targetPriority;
    }

    public Termite createTermite(Vector2 pos) {
        return new Termite(pos, world,entityQuery, destructionListener,targetPriority);
    }
}
