package se.chalmers.tda367.team15.game.controller;

import se.chalmers.tda367.team15.game.model.GameModel;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;
import se.chalmers.tda367.team15.game.view.ui.HudView;
import se.chalmers.tda367.team15.game.view.ui.PheromoneSelectionListener;

public class HudController implements PheromoneSelectionListener {
    private final HudView view;
    private final GameModel model;
    private final PheromoneController pheromoneController;

    public HudController(HudView view, GameModel model, PheromoneController pheromoneController) {
        this.view = view;
        this.model = model;
        this.pheromoneController = pheromoneController;

        initializeListeners();
    }

    private void initializeListeners() {
        view.setPheromoneSelectionListener(this);
    }

    @Override
    public void onPheromoneSelected(PheromoneType type) {
        pheromoneController.setCurrentType(type);
    }

    public void update(float dt) {
        view.updateData(model.getGameTime());
    }
}
