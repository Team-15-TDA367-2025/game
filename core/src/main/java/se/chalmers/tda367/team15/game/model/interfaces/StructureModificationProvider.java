package se.chalmers.tda367.team15.game.model.interfaces;

import se.chalmers.tda367.team15.game.model.structure.Structure;

public interface StructureModificationProvider extends StructureProvider {

    void addStructure(Structure structure);

    void removeStructure(Structure structure);

}
