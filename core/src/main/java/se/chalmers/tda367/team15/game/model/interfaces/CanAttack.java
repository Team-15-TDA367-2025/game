package se.chalmers.tda367.team15.game.model.interfaces;

import se.chalmers.tda367.team15.game.model.entity.Entity;

public interface CanAttack extends CanBeAttacked{
     Entity getEntity();
     int getVisionRadius();
     float getAttackDamage();
     float getAttackRange();
     int getAttackCoolDownMs();
}
