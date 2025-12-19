package se.chalmers.tda367.team15.game.model.interfaces.providers;

import java.util.List;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.HasPosition;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;

public interface ColonyDataProvider extends HasPosition {
    int getTotalResources(ResourceType type);

    List<Ant> getAnts();

    int getConsumption();
}
