package se.chalmers.tda367.team15.game.model.pheromones;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.GridPoint2;

public class PheromoneGrid {
    // Nested map: position -> (type -> pheromone)
    private final Map<GridPoint2, Map<PheromoneType, Pheromone>> pheromones;

    public PheromoneGrid() {
        this.pheromones = new HashMap<>();
    }

    /**
     * Checks if a pheromone of the given type exists at the position.
     */
    public boolean hasPheromoneAt(GridPoint2 pos, PheromoneType type) {
        Map<PheromoneType, Pheromone> typeMap = pheromones.get(pos);
        return typeMap != null && typeMap.containsKey(type);
    }

    /**
     * Gets a pheromone of a specific type at the given position.
     */
    public Pheromone getPheromoneAt(GridPoint2 pos, PheromoneType type) {
        Map<PheromoneType, Pheromone> typeMap = pheromones.get(pos);
        return typeMap != null ? typeMap.get(type) : null;
    }

    /**
     * Gets all pheromones at the given position (all types).
     */
    public Collection<Pheromone> getPheromonesAt(GridPoint2 pos) {
        Map<PheromoneType, Pheromone> typeMap = pheromones.get(pos);
        return typeMap != null ? new ArrayList<>(typeMap.values()) : List.of();
    }

    public void addPheromone(Pheromone pheromone) {
        GridPoint2 pos = pheromone.getPosition();
        pheromones.computeIfAbsent(pos, k -> new EnumMap<>(PheromoneType.class))
                .put(pheromone.getType(), pheromone);
    }

    /**
     * Removes a pheromone of a specific type at the position.
     */
    public void removePheromone(GridPoint2 pos, PheromoneType type) {
        Map<PheromoneType, Pheromone> typeMap = pheromones.get(pos);
        if (typeMap == null) {
            return;
        }

        typeMap.remove(type);
        if (typeMap.isEmpty()) {
            pheromones.remove(pos);
        }
    }

    public Collection<Pheromone> getAllPheromones() {
        List<Pheromone> allPheromones = new ArrayList<>();
        for (Map<PheromoneType, Pheromone> typeMap : pheromones.values()) {
            allPheromones.addAll(typeMap.values());
        }
        return allPheromones;
    }

    public List<Pheromone> getPheromonesIn3x3(GridPoint2 centerGridPos) {
        List<Pheromone> pheromonesInArea = new ArrayList<>();

        // Check all 9 cells in 3x3 grid (including center and all 8 neighbors)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                GridPoint2 pos = new GridPoint2(centerGridPos.x + dx, centerGridPos.y + dy);
                Map<PheromoneType, Pheromone> typeMap = pheromones.get(pos);
                if (typeMap != null) {
                    pheromonesInArea.addAll(typeMap.values());
                }
            }
        }
        return pheromonesInArea;
    }
}
