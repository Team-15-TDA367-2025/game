package se.chalmers.tda367.team15.game.view.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.Gdx;

import se.chalmers.tda367.team15.game.model.interfaces.TimeCycleDataProvider;

public class HudView {
    private final Stage stage;
    private final TopBarView topBar;
    private final BottomBarView bottomBar;
    private final Label fpsLabel;

    public HudView(SpriteBatch batch, UiSkin uiFactory) {
        stage = new Stage(new ScreenViewport(), batch);
        ((ScreenViewport) stage.getViewport()).setUnitsPerPixel(1f / UiTheme.UI_SCALE);

        // Create sub-views
        topBar = new TopBarView(uiFactory);
        bottomBar = new BottomBarView(uiFactory);

        // Layout TopBar
        Table topContainer = new Table();
        topContainer.setFillParent(true);
        topContainer.top();
        topContainer.padTop(UiTheme.PADDING_MEDIUM);
        topContainer.add(topBar);
        stage.addActor(topContainer);

        // Layout BottomBar
        bottomBar.addToStage(stage);

        // FPS Label
        Label.LabelStyle fpsStyle = uiFactory.createLabelStyle(0.75f, Color.WHITE);
        fpsLabel = new Label("FPS: 60", fpsStyle);

        Table fpsContainer = new Table();
        fpsContainer.setFillParent(true);
        fpsContainer.top().left();
        fpsContainer.add(fpsLabel).pad(UiTheme.PADDING_SMALL);
        stage.addActor(fpsContainer);
    }

    public void render(float dt) {
        fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        stage.act(dt);
        bottomBar.update(dt);
        stage.draw();
    }

    public void setEggPanelView(EggPanelView eggPanelView) {
        bottomBar.setEggPanelView(eggPanelView);
    }

    public BottomBarView getBottomBar() {
        return bottomBar;
    }

    public void resize(int w, int h) {
        stage.getViewport().update(w, h, true);
        bottomBar.updateLayout(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
    }

    public void updateData(TimeCycleDataProvider timeProvider, int antCount, int resourceCount, int consumption) {
        topBar.update(timeProvider, antCount, resourceCount, consumption);
    }

    public void setPheromoneSelectionListener(PheromoneSelectionListener listener) {
        bottomBar.setPheromoneSelectionListener(listener);
    }

    public void SetSpeedControlsListener(SpeedControlsListener speedControlsListener) {
        topBar.setSpeedControlsListener(speedControlsListener);
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        stage.dispose();
    }
}
