package se.chalmers.tda367.team15.game.model.entity;

import com.badlogic.gdx.math.Vector2;

import java.util.Vector;

public interface HasHealth {
    void takeDamage(float amount);
    void die();
    //Code duplication, but when we have instance of either structure or entity it makes code a lot cleaner.
    Vector2 getPosition();
}
