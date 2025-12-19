package se.chalmers.tda367.team15.game.model.interfaces.providers;

import java.util.List;

import se.chalmers.tda367.team15.game.model.structure.Structure;

public interface StructureProvider {
    /**
     * Returns all structures in the simulation.
     */
    List<Structure> getStructures();
}
