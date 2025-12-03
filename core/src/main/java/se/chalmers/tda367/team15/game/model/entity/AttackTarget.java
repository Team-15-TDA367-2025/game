package se.chalmers.tda367.team15.game.model.entity;

import se.chalmers.tda367.team15.game.model.CanBeAttacked;
import se.chalmers.tda367.team15.game.model.HasPosition;

/**
 * Contains information necessary for use in attack behaviour for one object. Both {@code canBeAttacked} and {@hasPosition}
 * is the same object exposing different functionality
 */
public class AttackTarget {
    public CanBeAttacked canBeAttacked;
    public HasPosition hasPosition;
    public AttackTarget(CanBeAttacked h, HasPosition p) {
        if(h != p) {
            throw new IllegalArgumentException("The CanBeAttacked and HasPosition must be the same Object!");
        }
        this.canBeAttacked=h;
        this.hasPosition=p;
    }

}
