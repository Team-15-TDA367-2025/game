package se.chalmers.tda367.team15.game.model.interfaces;

import java.util.Collection;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;

public interface PheromoneUsageProvider {
    PheromoneGridConverter getConverter();

    boolean addPheromone(GridPoint2 pos, PheromoneType type);

    void removePheromone(GridPoint2 pos, PheromoneType type);

    void removeAllPheromones(GridPoint2 pos);

    Pheromone getPheromoneAt(GridPoint2 pos, PheromoneType type);

    Collection<Pheromone> getPheromonesAt(GridPoint2 pos);

    Collection<Pheromone> getPheromones();
}
