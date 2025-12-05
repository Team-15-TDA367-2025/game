package se.chalmers.tda367.team15.game.view.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;

import se.chalmers.tda367.team15.game.controller.EggController;
import se.chalmers.tda367.team15.game.model.egg.Egg;
import se.chalmers.tda367.team15.game.model.egg.EggManager;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;
import se.chalmers.tda367.team15.game.model.structure.Colony;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;

/**
 * UI panel for purchasing eggs and displaying egg development progress.
 * Dynamically builds UI based on registered ant types in the registry.
 */
public class EggPanelView {
    private final UiFactory uiFactory;
    private final EggController eggController;
    private final EggManager eggManager;
    private final Colony colony;
    private final Table panelTable;
    private final HorizontalGroup eggTypeGroup;

    public EggPanelView(UiFactory uiFactory, EggController eggController, Colony colony) {
        this.uiFactory = uiFactory;
        this.eggController = eggController;
        this.colony = colony;
        this.eggManager = colony.getEggManager();
        
        panelTable = new Table();
        panelTable.setBackground(uiFactory.createDrawable("BottomBar/bottombar_bg"));
        
        eggTypeGroup = new HorizontalGroup();
        eggTypeGroup.space(UiTheme.BUTTON_SPACING);
        
        buildEggTypeButtons();
        
        panelTable.add(eggTypeGroup).pad(UiTheme.PADDING_MEDIUM);
    }

    /**
     * Dynamically builds purchase buttons for all registered ant types.
     */
    private void buildEggTypeButtons() {
        AntTypeRegistry registry = AntTypeRegistry.getInstance();
        
        for (AntType type : registry.getAll()) {
            VerticalGroup eggTypeContainer = createEggTypeButton(type);
            eggTypeGroup.addActor(eggTypeContainer);
        }
    }

    /**
     * Creates a button and info display for a specific ant type.
     *
     * @param type the ant type to create UI for
     * @return a VerticalGroup containing the button and labels
     */
    private VerticalGroup createEggTypeButton(AntType type) {
        // Create purchase button
        ImageButton button = uiFactory.createImageButton("BottomBar/btn1", () -> {
            eggController.purchaseEgg(type.id());
        });
        
        // Create labels
        Label nameLabel = new Label(type.displayName(),
                uiFactory.createLabelStyle(UiTheme.FONT_SCALE_DEFAULT, Color.WHITE));
        nameLabel.setAlignment(Align.center);
        
        Label costLabel = new Label("Cost: " + type.foodCost() + " Food",
                uiFactory.createLabelStyle(UiTheme.FONT_SCALE_DEFAULT * 0.8f, Color.LIGHT_GRAY));
        costLabel.setAlignment(Align.center);
        
        // Progress bar for eggs of this type
        ProgressBar.ProgressBarStyle progressStyle = new ProgressBar.ProgressBarStyle();
        // Use simple drawables for progress bar
        progressStyle.background = uiFactory.createDrawable("BottomBar/bottombar_bg");
        progressStyle.knobBefore = uiFactory.createDrawable("BottomBar/btn1");
        progressStyle.knob = uiFactory.createDrawable("BottomBar/btn1");
        ProgressBar progressBar = new ProgressBar(0f, 1f, 0.01f, false, progressStyle);
        progressBar.setValue(0f);
        progressBar.setSize(80f, 8f);
        progressBar.setVisible(false);
        
        // Store reference to progress bar for updates
        button.setUserObject(new EggTypeUIState(type.id(), progressBar));
        
        // Container
        VerticalGroup container = new VerticalGroup();
        container.center();
        container.space(UiTheme.PADDING_SMALL);
        container.addActor(button);
        container.addActor(nameLabel);
        container.addActor(costLabel);
        container.addActor(progressBar);
        
        return container;
    }

    /**
     * Updates the UI to reflect current egg states and resource availability.
     * Should be called each frame in render().
     */
    public void update() {
        // Update progress bars and button states
        for (int i = 0; i < eggTypeGroup.getChildren().size; i++) {
            VerticalGroup container = (VerticalGroup) eggTypeGroup.getChild(i);
            ImageButton button = (ImageButton) container.getChild(0);
            EggTypeUIState state = (EggTypeUIState) button.getUserObject();
            
            if (state != null) {
                // Find eggs of this type
                int eggCount = 0;
                float totalProgress = 0f;
                
                for (Egg egg : eggManager.getEggs()) {
                    if (egg.getTypeId().equals(state.typeId)) {
                        eggCount++;
                        totalProgress += egg.getProgress();
                    }
                }
                
                // Update progress bar
                ProgressBar progressBar = state.progressBar;
                if (eggCount > 0) {
                    progressBar.setValue(totalProgress / eggCount);
                    progressBar.setVisible(true);
                } else {
                    progressBar.setVisible(false);
                }
                
                // Update button enabled state based on resources
                AntType type = AntTypeRegistry.getInstance().get(state.typeId);
                if (type != null) {
                    boolean canAfford = colony.getTotalResources(ResourceType.FOOD) >= type.foodCost();
                    button.setDisabled(!canAfford);
                }
            }
        }
    }

    /**
     * Gets the table containing the panel UI.
     *
     * @return the panel table
     */
    public Table getTable() {
        return panelTable;
    }

    /**
     * Helper class to store UI state for an egg type.
     */
    private static class EggTypeUIState {
        final String typeId;
        final ProgressBar progressBar;

        EggTypeUIState(String typeId, ProgressBar progressBar) {
            this.typeId = typeId;
            this.progressBar = progressBar;
        }
    }
}
