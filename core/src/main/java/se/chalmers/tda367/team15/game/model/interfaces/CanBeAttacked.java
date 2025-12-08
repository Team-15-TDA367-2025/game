package se.chalmers.tda367.team15.game.model.interfaces;

import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.faction.Faction;

/**
 * Represents the notion that the Object can be destroyed or die in the game
 * world by attackers.
 */
public interface CanBeAttacked {
    /**
     * Instructs the object to take damage.
     *
     * @param amount the damage taken.
     */
    void takeDamage(float amount);

    /**
     * Instructs the object to notify the
     * {@link se.chalmers.tda367.team15.game.model.DestructionListener} to remove it
     * from the {@link se.chalmers.tda367.team15.game.model.GameWorld}
     */
    void die();

    /**
     *
     * @return the {@link AttackCategory} of the attackable object.
     */
    AttackCategory getAttackCategory();

    /**
     *
     * @return the {@link Faction} of the attackable object.
     */
    Faction getFaction();
}
