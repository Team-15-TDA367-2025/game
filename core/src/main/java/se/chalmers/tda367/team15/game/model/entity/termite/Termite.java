package se.chalmers.tda367.team15.game.model.entity.termite;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.DestructionListener;
import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.faction.Faction;
import se.chalmers.tda367.team15.game.model.interfaces.CanAttack;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.managers.StructureManager;

import java.util.HashMap;

/**
 * Termites are hostile to anything not in their {@link Faction}, termites
 * {@link Faction} is "TERMITE_PROTECTORATE". Termites will pursue enemy
 * entities
 * then structures, then stand still. Perfect vision of map.
 */
public class Termite extends Entity implements CanAttack {
    private final int visionRadius = 1000000000;
    private final Faction faction = Faction.TERMITE_PROTECTORATE;

    private final float speed= 2.9f;
    private final float MAX_HEALTH = 1;
    private float health= MAX_HEALTH;
    private final DestructionListener destructionListener;
    private TermiteAttackBehaviour termiteAttackBehaviour;

    public Termite(Vector2 position, EntityQuery entityQuery, StructureManager structureManager, HashMap<AttackCategory, Integer> targetPriority ,DestructionListener destructionListener) {
        super(position, "termite");
        this.destructionListener = destructionListener;
        this.termiteAttackBehaviour=new TermiteAttackBehaviour(this,entityQuery,structureManager,targetPriority);
    }

    /**
     * Updates the termite
     *
     */
    @Override
    public void update(float deltaTime) {
        termiteAttackBehaviour.update();
        super.update(deltaTime);

    }

    /**
     *
     * @return returns termites speed.
     */
    @Override
    public float getSpeed() {
        return speed;
    }

    /**
     *
     * @return the termites {@link Faction}
     */
    @Override
    public Faction getFaction() {
        return faction;
    }

    @Override
    public void takeDamage(float amount) {
        health = Math.max(0f, health - amount);
        if (health == 0f) {
            die();
        }
    }

    @Override
    public void die() {
        health = 0f;
        destructionListener.notifyEntityDeathObservers(this);
    }

    @Override
    public AttackCategory getAttackCategory() {
        return AttackCategory.TERMITE;
    }

    @Override
    public Vector2 getSize() {
        return new Vector2(1f, 1.5f);
    }

    @Override
    public void setVelocity(Vector2 v) {
     super.setVelocity(v);
    }


    @Override
    public int getVisionRadius() {
        return visionRadius;
    }

    @Override
    public float getAttackDamage() {
        return 2;
    }

    @Override
    public float getAttackRange() {
        return 2;
    }

    @Override
    public int getAttackCoolDownMs() {
        return 1000;
    }
}
