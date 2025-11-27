package se.chalmers.tda367.team15.game.model.entity.Termite;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import se.chalmers.tda367.team15.game.model.GameWorld;
import se.chalmers.tda367.team15.game.model.faction.Faction;
import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.interfaces.Updatable;
import se.chalmers.tda367.team15.game.model.structure.Structure;
import se.chalmers.tda367.team15.game.model.structure.Colony;
import se.chalmers.tda367.team15.game.model.entity.Entity;


import java.util.ArrayList;
import java.util.List;

public class Termite extends Entity {
    Faction faction = Faction.TERMITE_PROTECTORATE;
    private static final float SPEED = 5f;
    //HealthComponent healthComponent;
    //TermiteBehaviour termiteBehaviour;
    //AttackComponent attackComponent;

    public Termite(Vector2 position,GameWorld gameWorld) {
        super(position, "Termite", gameWorld);
    }

    @Override
    public void update(float deltaTime){
        Vector2 targetV = new Vector2(50,50);

        List<Entity> entities = getGameWorld().getEntities();

        // check for hostile entities.there might be ants we can eat! *licks lips with devious smile* >:)
        List<Entity> targetEntities = hostileEntities(entities);
        if(!targetEntities.isEmpty()) {
            Entity closestEntity= targetEntities.getFirst();

            for(Entity e : targetEntities) {
                float dst = e.getPosition().dst(position);
                if(dst < closestEntity.getPosition().dst(position) ) {
                    closestEntity = e;
                }
            }
            targetV = closestEntity.getPosition().sub(position);
        }



        // move to target
        // TODO might overshoot, TOO BAD!
        if(targetV.len() > 0.01f) {
            velocity.set(targetV.nor().scl(SPEED));
        }
        super.update(deltaTime);
    }


    List<Entity> hostileEntities(List<Entity> entities) {
        List<Entity> targetEntities = new ArrayList<>();
        for (Entity e : entities) {
            if (e.getFaction() != this.faction) {
                targetEntities.add(e);
            }
        }
        return targetEntities;
    }

}

 /*List<Structure> structures = getGameWorld().getStructures();
            for(Structure s: structures) {
                // TODO, TOO BAD!!!
                if(s.getClass().isInstance(new Colony(new GridPoint2(0,0)))) {
                    targetV = s.getPosition().sub(position);
                }
            }*/
