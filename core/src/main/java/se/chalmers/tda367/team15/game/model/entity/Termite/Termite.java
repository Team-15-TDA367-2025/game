package se.chalmers.tda367.team15.game.model.entity.Termite;

import com.badlogic.gdx.math.Vector2;
import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.DestructionListener;
import se.chalmers.tda367.team15.game.model.GameWorld;
import se.chalmers.tda367.team15.game.model.entity.AttackComponent;
import se.chalmers.tda367.team15.game.model.entity.AttackTarget;
import se.chalmers.tda367.team15.game.model.interfaces.CanBeAttacked;
import se.chalmers.tda367.team15.game.model.faction.Faction;
import se.chalmers.tda367.team15.game.model.structure.Structure;
import se.chalmers.tda367.team15.game.model.entity.Entity;


import java.util.List;

/**
 * Termites are hostile to anything not in their {@link Faction}, termites {@link Faction} is "TERMITE_PROTECTORATE". Termites will pursue enemy entities
 *  then structures, then stand still. Perfect vision of map.
 */
public class Termite extends Entity implements CanBeAttacked {
    private final Faction faction = Faction.TERMITE_PROTECTORATE;
    private final float SPEED = 2.9f;
    private final TermiteBehaviour  termiteBehaviour;
    private AttackComponent attackComponent = new AttackComponent(5, 1000, 2.0f, this);
    private final float MAX_HEALTH = 1;
    private float health;
    public Termite(Vector2 position) {
        super(position, "termite");
        this.termiteBehaviour = new TermiteBehaviour(this);
        health = MAX_HEALTH;
    }

    /**
     * Updates the termite
     * @param deltaTime the real time change between frames
     */
    @Override
    public void update(float deltaTime){
        List<Entity> entities = GameWorld.getInstance().getEntities();
        List<Structure> structures = GameWorld.getInstance().getStructures();
        AttackTarget target = termiteBehaviour.update(entities,structures);
        super.update(deltaTime);
        if(target != null) {
            attackComponent.attack(target);
        }

    }

    /**
     *
     * @return returns termites speed.
     */
    public float getSpeed() {
        return SPEED;
    }

    /**
     *
     * @return the termites {@link Faction}
     */
    @Override
    public Faction getFaction(){
        return faction;
    }


    @Override
    public void takeDamage(float amount) {
        health = Math.max(0f,health-amount);
        if(health == 0f) {
            die();
        }
    }


    @Override
    public void die() {
        health = 0f;
        DestructionListener.getInstance().notifyEntityDeathObservers(this);
    }

    @Override
    public AttackCategory getAttackCategory() {
        return AttackCategory.TERMITE;
    }

    @Override
    public Vector2 getSize() {
        return new Vector2(1f, 1.5f);
    }
}


