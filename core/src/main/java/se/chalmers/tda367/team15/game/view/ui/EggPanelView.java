package se.chalmers.tda367.team15.game.view.ui;

import java.util.Optional;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import se.chalmers.tda367.team15.game.controller.EggController;
import se.chalmers.tda367.team15.game.model.Egg;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;
import se.chalmers.tda367.team15.game.model.interfaces.providers.ColonyDataProvider;
import se.chalmers.tda367.team15.game.model.managers.EggManager;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;
import se.chalmers.tda367.team15.game.view.TextureResolver;

/**
 * UI panel for purchasing eggs and displaying egg development progress.
 * Dynamically builds UI based on registered ant types in the registry.
 */
public class EggPanelView {
    private final UiSkin uiSkin;
    private final EggController eggController;
    private final EggManager eggManager;
    private final Table panelTable;
    private final HorizontalGroup eggTypeGroup;
    private final ColonyDataProvider colonyDataProvider;
    private final AntTypeRegistry antTypeRegistry;

    public EggPanelView(UiSkin uiFactory, EggController eggController, EggManager eggManager,
            ColonyDataProvider colonyDataProvider, AntTypeRegistry antTypeRegistry, TextureResolver textureResolver) {
        this.uiSkin = uiFactory;
        this.eggController = eggController;
        this.eggManager = eggManager;
        this.colonyDataProvider = colonyDataProvider;
        this.antTypeRegistry = antTypeRegistry;
        panelTable = new Table();
        // No background - this panel is embedded in BottomBarView which has its own
        // background

        eggTypeGroup = new HorizontalGroup();
        eggTypeGroup.space(UiTheme.BUTTON_SPACING);

        buildEggTypeButtons();

        panelTable.add(eggTypeGroup);
    }

    /**
     * Dynamically builds purchase buttons for all registered ant types.
     */
    private void buildEggTypeButtons() {
        for (AntType type : antTypeRegistry.getAll()) {
            Table eggTypeContainer = createEggTypeButton(type);
            eggTypeGroup.addActor(eggTypeContainer);
        }
    }

    /**
     * Creates a button and info display for a specific ant type.
     *
     * @param type the ant type to create UI for
     * @return a Table containing the button and name label
     */
    private Table createEggTypeButton(AntType type) {
        // Create purchase button with the ant type's texture
        ImageButton button = uiSkin.createImageButton(type.id(), () -> {
            eggController.purchaseEgg(type.id());
        });

        // Name label below the button
        String labelText = type.displayName() + " (" + type.foodCost() + ")";
        Label nameLabel = new Label(labelText,
                uiSkin.createLabelStyle(UiTheme.FONT_SCALE_DEFAULT, Color.WHITE));

        // Progress bar for eggs of this type - using area background for consistent
        // styling
        ProgressBar.ProgressBarStyle progressStyle = new ProgressBar.ProgressBarStyle();
        progressStyle.background = uiSkin.getAreaBackground();
        progressStyle.knobBefore = uiSkin.getButtonBackgroundChecked();
        ProgressBar progressBar = new ProgressBar(0f, 1f, 0.01f, false, progressStyle);
        progressBar.setValue(0f);
        progressBar.setVisible(false);

        // Store reference to progress bar for updates
        button.setUserObject(new EggTypeUIState(type.id(), progressBar));

        // Container table for vertical layout
        Table container = new Table();
        container.add(button).size(UiTheme.ICON_SIZE_LARGE).row();
        container.add(nameLabel).padTop(UiTheme.PADDING_TINY).row();
        container.add(progressBar).width(UiTheme.ICON_SIZE_LARGE).height(UiTheme.PADDING_SMALL)
                .padTop(UiTheme.PADDING_TINY);

        return container;
    }

    /**
     * Updates the UI to reflect current egg states and resource availability.
     * Should be called each frame in render().
     */
    public void update() {
        // Update progress bars and button states
        for (int i = 0; i < eggTypeGroup.getChildren().size; i++) {
            Table container = (Table) eggTypeGroup.getChild(i);
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

                Optional<AntType> type = antTypeRegistry.get(state.typeId);
                if (type.isPresent()) {
                    boolean canAfford = colonyDataProvider.getTotalResources(ResourceType.FOOD) >= type.orElseThrow().foodCost();
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
