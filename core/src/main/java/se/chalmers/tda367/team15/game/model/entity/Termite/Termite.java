package se.chalmers.tda367.team15.game.model.entity.Termite;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import se.chalmers.tda367.team15.game.model.GameWorld;
import se.chalmers.tda367.team15.game.model.entity.AttackComponent;
import se.chalmers.tda367.team15.game.model.entity.HasHealth;
import se.chalmers.tda367.team15.game.model.faction.Faction;
import se.chalmers.tda367.team15.game.model.structure.Structure;
import se.chalmers.tda367.team15.game.model.structure.Colony;
import se.chalmers.tda367.team15.game.model.entity.Entity;


import java.util.ArrayList;
import java.util.List;

public class Termite extends Entity {
    private final Faction faction = Faction.TERMITE_PROTECTORATE;
    private final float SPEED = 5.3f;
    private final int visionRadius = 4;
    private TermiteBehaviour termiteBehaviour;
    private AttackComponent attackComponent = new AttackComponent(2,1000,5,this);

    public Termite(Vector2 position,GameWorld gameWorld) {
        super(position, "Termite", gameWorld);
        this.termiteBehaviour = new TermiteBehaviour(this);
    }

    @Override
    public void update(float deltaTime){
        List<Entity> entities = getGameWorld().getEntities();
        List<Structure> structures = getGameWorld().getStructures();
        HasHealth target = termiteBehaviour.update(entities,structures);
        super.update(deltaTime);
        if(target != null) {
            attackComponent.attack(target);
        }

    }

    public float getSpeed() {
        return SPEED;
    }

    @Override
    public Faction getFaction(){
        return faction;
    }

    public AttackComponent getAttackComponent() {
        return attackComponent;
    }
}


