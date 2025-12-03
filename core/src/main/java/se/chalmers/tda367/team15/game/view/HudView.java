package se.chalmers.tda367.team15.game.view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import se.chalmers.tda367.team15.game.model.TimeCycle;

public class HudView {
    private final TopBarView topBar;
    private final BottomBarView bottomBar;

    public HudView(SpriteBatch batch) {
        topBar = new TopBarView(batch);
        bottomBar = new BottomBarView(batch);
    }

    public void render(float dt) {
        topBar.render(dt);
        bottomBar.render(dt);
    }

    public void resize(int w, int h) {
        topBar.resize(w, h);
        bottomBar.resize(w, h);
    }

    public void updateData(TimeCycle.GameTime gameTime) {

        topBar.update(gameTime, 0, 0);
    }

    public Stage getTopStage() { return topBar.getStage(); }
    public Stage getBottomStage() { return bottomBar.getStage(); }

    public void dispose() {
        topBar.dispose();
        bottomBar.dispose();
    }
}
