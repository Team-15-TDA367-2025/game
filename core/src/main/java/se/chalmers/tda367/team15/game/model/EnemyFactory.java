package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.termite.Termite;

public class EnemyFactory {
    private GameWorld world;

    public EnemyFactory(GameWorld world) {
        this.world = world;
    }

    public Termite createTermite(Vector2 pos) {
        return new Termite(pos, world);
    }
}
