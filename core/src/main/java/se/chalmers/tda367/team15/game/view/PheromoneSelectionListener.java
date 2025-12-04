package se.chalmers.tda367.team15.game.view;

import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;

/**
 * Listener interface for pheromone type selection events from the UI.
 */
public interface PheromoneSelectionListener {
    /**
     * Called when a pheromone type is selected.
     * @param type The selected pheromone type, or null for delete mode.
     */
    void onPheromoneSelected(PheromoneType type);
}

