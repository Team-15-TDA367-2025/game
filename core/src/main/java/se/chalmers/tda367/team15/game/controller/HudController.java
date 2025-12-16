package se.chalmers.tda367.team15.game.controller;

import se.chalmers.tda367.team15.game.model.TimeCycle;
import se.chalmers.tda367.team15.game.model.egg.EggManager;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;
import se.chalmers.tda367.team15.game.model.interfaces.ColonyUsageProvider;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;
import se.chalmers.tda367.team15.game.view.ui.EggPanelView;
import se.chalmers.tda367.team15.game.view.ui.HudView;
import se.chalmers.tda367.team15.game.view.ui.PheromoneSelectionListener;
import se.chalmers.tda367.team15.game.view.ui.UiFactory;

public class HudController implements PheromoneSelectionListener {
    private final HudView view;
    private final PheromoneController pheromoneController;
    private final EggController eggController;
    private final EggPanelView eggPanelView;
    private final ColonyUsageProvider colonyUsageProvider;
    private final SpeedController speedController;
    private final TimeCycle timeCycle;

    public HudController(HudView view, AntTypeRegistry antTypeRegistry, EggManager eggManager, PheromoneController pheromoneController,SpeedController speedController, UiFactory uiFactory, TimeCycle timeCycle, ColonyUsageProvider colonyUsageProvider) {
        this.view = view;
        this.timeCycle = timeCycle;
        this.pheromoneController = pheromoneController;
        this.speedController=speedController;
        this.colonyUsageProvider = colonyUsageProvider;
        // Create egg controller and panel
        this.eggController = new EggController(antTypeRegistry, colonyUsageProvider);
        this.eggPanelView = new EggPanelView(uiFactory, eggController, eggManager, colonyUsageProvider, antTypeRegistry);

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
        view.updateData(this.timeCycle.getGameTime(), colonyUsageProvider.getTotalAnts(),
                colonyUsageProvider.getTotalResources(ResourceType.FOOD), colonyUsageProvider.getConsumption());
    }
}
