package se.chalmers.tda367.team15.game.model.interfaces;

import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;

public interface ColonyDataProvider extends HasPosition {
    int getTotalResources(ResourceType type);

    int getTotalAnts();

    int getConsumption();
}
