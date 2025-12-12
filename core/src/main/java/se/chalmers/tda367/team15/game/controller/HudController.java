package se.chalmers.tda367.team15.game.controller;

import se.chalmers.tda367.team15.game.model.GameModel;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;
import se.chalmers.tda367.team15.game.view.ui.EggPanelView;
import se.chalmers.tda367.team15.game.view.ui.HudView;
import se.chalmers.tda367.team15.game.view.ui.PheromoneSelectionListener;
import se.chalmers.tda367.team15.game.view.ui.UiFactory;

public class HudController implements PheromoneSelectionListener {
    private final HudView view;
    private final GameModel model;
    private final PheromoneController pheromoneController;
    private final EggController eggController;
    private final EggPanelView eggPanelView;
    private final SpeedController speedController;

    public HudController(HudView view, GameModel model, PheromoneController pheromoneController,SpeedController speedController, UiFactory uiFactory) {
        this.view = view;
        this.model = model;
        this.pheromoneController = pheromoneController;
        this.speedController=speedController;
        // Create egg controller and panel
        this.eggController = new EggController(model.getColony());
        this.eggPanelView = new EggPanelView(uiFactory, eggController, model.getColony());

        initializeListeners();
    }

    private void initializeListeners() {
        view.setPheromoneSelectionListener(this);
        view.setEggPanelView(eggPanelView);
        view.SetSpeedControlsListener(speedController);
    }

    @Override
    public void onPheromoneSelected(PheromoneType type) {
        pheromoneController.setCurrentType(type);
    }

    public void update(float dt) {
        view.updateData(model.getGameTime(), model.getColony().getAntCount(),
                model.getColony().getTotalResources(ResourceType.FOOD), model.getColony().calculateConsumption());
    }
}
