package se.chalmers.tda367.team15.game.model.interfaces;

import se.chalmers.tda367.team15.game.model.structure.Structure;

public interface StructureDeathObserver {
    void onStructureDeath(Structure s);
}
