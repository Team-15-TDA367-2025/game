package se.chalmers.tda367.team15.game.view.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import se.chalmers.tda367.team15.game.model.TimeCycle;

public class HudView {
    private final Stage stage;
    private final TopBarView topBar;
    private final BottomBarView bottomBar;

    public HudView(SpriteBatch batch, UiFactory uiFactory) {
        stage = new Stage(new ScreenViewport(), batch);

        // Create sub-views
        topBar = new TopBarView(uiFactory);
        bottomBar = new BottomBarView(uiFactory);

        // Layout TopBar
        Table topContainer = new Table();
        topContainer.setFillParent(true);
        topContainer.top();
        topContainer.add(topBar).width(UiTheme.TOP_BAR_WIDTH).height(UiTheme.TOP_BAR_HEIGHT);
        stage.addActor(topContainer);

        // Layout BottomBar
        bottomBar.addToStage(stage);
    }

    public void render(float dt) {
        stage.act(dt);
        stage.draw();
    }

    public void resize(int w, int h) {
        stage.getViewport().update(w, h, true);
        bottomBar.updateLayout(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
    }

    public void updateData(TimeCycle.GameTime gameTime) {
        topBar.update(gameTime, 0, 0);
    }

    public void setPheromoneSelectionListener(PheromoneSelectionListener listener) {
        bottomBar.setPheromoneSelectionListener(listener);
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        stage.dispose();
    }
}
