package se.chalmers.tda367.team15.game.model.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.chalmers.tda367.team15.game.model.interfaces.StructureModificationProvider;
import se.chalmers.tda367.team15.game.model.interfaces.SimulationObserver;
import se.chalmers.tda367.team15.game.model.structure.Structure;

/**
 * Manages the lifecycle of entities in the simulation.
 * Owns all entities, handles updates, and cleans up on death.
 */
public class StructureManager implements SimulationObserver, StructureModificationProvider {
    private final List<Structure> structures = new ArrayList<>();

    public void addStructure(Structure structure) {
        structures.add(structure);
    }

    public List<Structure> getStructures() {
        return Collections.unmodifiableList(structures);
    }

    public void removeStructure(Structure structure) {
        structures.remove(structure);
    }

    @Override
    public void update(float deltaTime) {
        List<Structure> toUpdate = new ArrayList<>(structures);
        for (Structure structure : toUpdate) {
            structure.update(deltaTime);
        }
    }

}
