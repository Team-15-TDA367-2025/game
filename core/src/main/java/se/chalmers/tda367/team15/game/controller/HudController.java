package se.chalmers.tda367.team15.game.controller;

import se.chalmers.tda367.team15.game.model.egg.EggManager;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;
import se.chalmers.tda367.team15.game.model.interfaces.ColonyDataProvider;
import se.chalmers.tda367.team15.game.model.interfaces.EggPurchaseProvider;
import se.chalmers.tda367.team15.game.model.interfaces.TimeCycleDataProvider;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;
import se.chalmers.tda367.team15.game.view.ui.EggPanelView;
import se.chalmers.tda367.team15.game.view.ui.HudView;
import se.chalmers.tda367.team15.game.view.ui.PheromoneSelectionListener;
import se.chalmers.tda367.team15.game.view.ui.UiSkin;

public class HudController implements PheromoneSelectionListener {
    private final HudView view;
    private final PheromoneController pheromoneController;
    private final EggController eggController;
    private final EggPanelView eggPanelView;
    private final ColonyDataProvider colonyDataProvider;
    private final SpeedController speedController;
    private final TimeCycleDataProvider timeProvider;

    public HudController(HudView view, AntTypeRegistry antTypeRegistry, EggManager eggManager,
            PheromoneController pheromoneController, SpeedController speedController, UiSkin uiFactory,
            TimeCycleDataProvider timeProvider, ColonyDataProvider colonyDataProvider, EggPurchaseProvider eggPurchaseProvider) {
        this.view = view;
        this.timeProvider = timeProvider;
        this.pheromoneController = pheromoneController;
        this.speedController = speedController;
        this.colonyDataProvider = colonyDataProvider;
        // Create egg controller and panel
        this.eggController = new EggController(antTypeRegistry, eggPurchaseProvider);
        this.eggPanelView = new EggPanelView(uiFactory, eggController, eggManager, colonyDataProvider,
                antTypeRegistry);

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
        view.updateData(timeProvider, colonyDataProvider.getAnts().size(),
                colonyDataProvider.getTotalResources(ResourceType.FOOD), colonyDataProvider.getConsumption());
    }
}
