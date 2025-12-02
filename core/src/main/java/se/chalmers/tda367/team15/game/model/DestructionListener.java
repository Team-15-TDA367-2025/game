package se.chalmers.tda367.team15.game.model;

import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.structure.Structure;

import java.util.LinkedHashSet;

/**
 * Has the responsibility of notifying interested parties when something dies / is destroyed.
 */
public class DestructionListener  {
    static DestructionListener destructionListener;
    private LinkedHashSet<EntityDeathObserver> entityObservers;
    private LinkedHashSet<StructureDeathObserver> structureDeathObservers;

    private DestructionListener() {
        this.entityObservers = new LinkedHashSet<>();
        structureDeathObservers= new LinkedHashSet<>();
    }

    static public DestructionListener getInstance() {
        if(destructionListener == null) {
            destructionListener= new DestructionListener();
        }
       return destructionListener;
    }

    public void addEntityDeathObserver(EntityDeathObserver obs) {
        entityObservers.add(obs);
    }


    public void removeEntityDeathObserver(EntityDeathObserver obs) {
        entityObservers.remove(obs);
    }

    public void addStructureDeathObserver(StructureDeathObserver obs) {
        structureDeathObservers.add(obs);
    }


    public void removeStructureDeathObserver(StructureDeathObserver obs) {
        structureDeathObservers.remove(obs);
    }

    public void notifyEntityDeathObservers(Entity e){
        for(EntityDeathObserver edo: entityObservers) {
            edo.onEntityDeath(e);
        }
    }

    public void notifyStructureDeathObservers(Structure s) {
        for(StructureDeathObserver sdo: structureDeathObservers) {
            sdo.onStructureDeath(s);
        }
    }
}
