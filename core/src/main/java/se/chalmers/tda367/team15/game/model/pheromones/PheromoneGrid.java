package se.chalmers.tda367.team15.game.model.pheromones;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.GridPoint2;

public class PheromoneGrid {
    private final Map<GridPoint2, Pheromone> pheromones;

    public PheromoneGrid() {
        this.pheromones = new HashMap<>();
    }

    public boolean hasPheromoneAt(GridPoint2 pos) {
        return pheromones.containsKey(pos);
    }

    public Pheromone getPheromoneAt(GridPoint2 pos) {
        return pheromones.get(pos);
    }

    public void addPheromone(Pheromone pheromone) {
        pheromones.put(pheromone.getPosition(), pheromone);
    }

    public void removePheromone(GridPoint2 pos) {
        pheromones.remove(pos);
    }

    public Collection<Pheromone> getAllPheromones() {
        return new ArrayList<>(pheromones.values());
    }

    public List<Pheromone> getPheromonesIn3x3(GridPoint2 centerGridPos) {
        List<Pheromone> pheromonesInArea = new ArrayList<>();

        // Check all 9 cells in 3x3 grid (including center and all 8 neighbors)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                GridPoint2 pos = new GridPoint2(centerGridPos.x + dx, centerGridPos.y + dy);
                Pheromone pheromone = pheromones.get(pos);
                if (pheromone != null) {
                    pheromonesInArea.add(pheromone);
                }
            }
        }
        return pheromonesInArea;
    }
}
