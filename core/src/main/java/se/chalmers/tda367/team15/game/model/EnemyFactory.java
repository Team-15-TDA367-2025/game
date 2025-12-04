package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.Termite.Termite;

public class EnemyFactory {

    public EnemyFactory() {
    }

    public Termite createTermite(Vector2 pos) {
        return new Termite(pos);
    }
}
