package se.chalmers.tda367.team15.game.model.entity.Termite;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.math.Vector2;
import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.HasHealth;
import se.chalmers.tda367.team15.game.model.structure.Colony;
import se.chalmers.tda367.team15.game.model.structure.Structure;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class TermiteBehaviour {
    Termite termite;
    TermiteBehaviour(Termite termite){
        this.termite = termite;
    }

    public HasHealth update(List<Entity> entities,List<Structure> structures) {

        Vector2 targetV = termite.getPosition();
        Object tmp = null;

        // check for hostile entities.there might be ants we can eat! *licks lips with devious smile* >:)
        Entity tE = determineTargetE(entities);
        Structure tS = determineTargetS(structures);

        // determine target, entities first, then structures,  then stand still.
        if(tE != null) {
            targetV.set(tE.getPosition());
            tmp = tE;
        }
        else {
            if(determineTargetS(structures) != null) {
                targetV.set(tS.getPosition());
                tmp = tS;
            }
        }

        Vector2 diff = targetV.sub(termite.getPosition());
        // set velocity
        if(targetV.len() > 0.01f) {
            termite.setVelocity(diff.nor().scl(termite.getSpeed()));
        }

        // https://www.youtube.com/watch?v=pOp3hJz6ROY
        return (HasHealth) tmp;
    }

    private Entity determineTargetE(List<Entity> entities){
        Entity target = null;
        List<Entity> targetEntities = potentialTargetsE(entities);

        if(!targetEntities.isEmpty()) {
            Entity closestEntity = targetEntities.getFirst();
            for (Entity e : targetEntities) {
                float dst = e.getPosition().dst(termite.getPosition());
                if (dst < closestEntity.getPosition().dst(termite.getPosition())) {
                    closestEntity = e;
                }
            }
            target = closestEntity;
        }
        return target;
    }

    private Structure determineTargetS(List<Structure> structures){
        Structure target = null;
        List<Structure> potentialTargets = potentialTargetsS(structures);
        for (Structure s : potentialTargets ) {
            if (s instanceof Colony) {
                target = s;
            }
        }
        return target;
    }

    private List<Entity> potentialTargetsE(List<Entity> entities) {
            List<Entity> targetEntities = new ArrayList<>();

            for (Entity e : entities) {
                if(e instanceof HasHealth) {
                    if (e.getFaction() != termite.getFaction()) {
                        targetEntities.add(e);
                    }
                }

            }
            return targetEntities;
    }
    private List<Structure> potentialTargetsS(List<Structure> structures){
        List<Structure> targets = new ArrayList<>();
        for (Structure s : structures) {
            if(s instanceof HasHealth) {
                if(s.getFaction() != termite.getFaction()) {
                    targets.add(s);
                }
            }
        }
        return targets;

    }



}
