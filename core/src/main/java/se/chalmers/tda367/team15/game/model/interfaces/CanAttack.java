package se.chalmers.tda367.team15.game.model.interfaces;

import com.badlogic.gdx.math.Vector2;

/**
 * Represents the notion that a thing can attack.
 */
public interface CanAttack extends CanBeAttacked{
     int getVisionRadius();
     float getAttackDamage();
     float getAttackRange();
     int getAttackCoolDownMs();
    float getSpeed();
    void setVelocity(Vector2 v);
}
