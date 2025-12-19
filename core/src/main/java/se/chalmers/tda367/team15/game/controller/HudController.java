package se.chalmers.tda367.team15.game.controller;

import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;
import se.chalmers.tda367.team15.game.model.interfaces.providers.ColonyDataProvider;
import se.chalmers.tda367.team15.game.model.interfaces.providers.TimeCycleDataProvider;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;
import se.chalmers.tda367.team15.game.view.ui.EggPanelView;
import se.chalmers.tda367.team15.game.view.ui.HudView;
import se.chalmers.tda367.team15.game.view.ui.PheromoneSelectionListener;
import se.chalmers.tda367.team15.game.view.ui.UiSkin;

public class HudController implements PheromoneSelectionListener {
    private final HudView view;
    private final PheromoneController pheromoneController;
    private final ColonyDataProvider colonyDataProvider;
    private final SpeedController speedController;
    private final TimeCycleDataProvider timeProvider;
    private final EggController eggController;
    private final EggPanelView eggPanelView;

    public HudController(HudView view, AntTypeRegistry antTypeRegistry,
            PheromoneController pheromoneController, SpeedController speedController, UiSkin uiFactory,
            TimeCycleDataProvider timeProvider, ColonyDataProvider colonyDataProvider,
            EggController eggController,
            EggPanelView eggPanelView) {
        this.view = view;
        this.timeProvider = timeProvider;
        this.pheromoneController = pheromoneController;
        this.speedController = speedController;
        this.colonyDataProvider = colonyDataProvider;
        this.eggPanelView = eggPanelView;
        this.eggController = eggController;

        initializeListeners();
    }

    private void initializeListeners() {
        view.setPheromoneSelectionListener(this);
        eggPanelView.setEggPanelListener(eggController);
        view.setSpeedControlsListener(speedController);
        view.setEggPanelView(eggPanelView);
    }

    @Override
    public void onPheromoneSelected(PheromoneType type) {
        pheromoneController.setCurrentType(type);
    }

    public void update(float dt) {
        view.updateData(timeProvider, colonyDataProvider.getAnts().size(),
                colonyDataProvider.getTotalResources(ResourceType.FOOD), colonyDataProvider.getConsumption());
    }
}
