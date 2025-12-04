package se.chalmers.tda367.team15.game.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

import se.chalmers.tda367.team15.game.controller.CameraController;
import se.chalmers.tda367.team15.game.controller.HudController;
import se.chalmers.tda367.team15.game.model.GameModel;
import se.chalmers.tda367.team15.game.view.TextureRegistry;
import se.chalmers.tda367.team15.game.view.camera.CameraView;
import se.chalmers.tda367.team15.game.view.camera.ViewportListener;
import se.chalmers.tda367.team15.game.view.ui.HudView;
import se.chalmers.tda367.team15.game.view.ui.UiFactory;
import se.chalmers.tda367.team15.game.view.renderers.PheromoneRenderer;
import se.chalmers.tda367.team15.game.view.renderers.WorldRenderer;

/**
 * Main game screen.
 * Dependencies are injected via constructor to keep the class simple and
 * testable.
 */
public class GameScreen extends ScreenAdapter {

    // Models
    private final GameModel gameModel;

    // Views
    private final CameraView cameraView;
    private final WorldRenderer sceneView;
    private final PheromoneRenderer pheromoneView;
    private final HudView hudView;

    // Resources
    private final TextureRegistry textureRegistry;
    private final UiFactory uiFactory;
    private final ViewportListener viewportListener;

    // Controllers
    private final CameraController cameraController;
    private final HudController hudController;

    public GameScreen(
            GameModel gameModel,
            CameraView cameraView,
            WorldRenderer sceneView,
            PheromoneRenderer pheromoneView,
            HudView hudView,
            TextureRegistry textureRegistry,
            UiFactory uiFactory,
            ViewportListener viewportListener,
            CameraController cameraController,
            HudController hudController) {
        this.gameModel = gameModel;
        this.cameraView = cameraView;
        this.sceneView = sceneView;
        this.pheromoneView = pheromoneView;
        this.hudView = hudView;
        this.textureRegistry = textureRegistry;
        this.uiFactory = uiFactory;
        this.viewportListener = viewportListener;
        this.cameraController = cameraController;
        this.hudController = hudController;
    }

    @Override
    public void render(float delta) {
        // Update
        cameraController.update(delta);
        cameraView.updateCamera();
        gameModel.update(delta);
        hudController.update(delta);

        // Render
        ScreenUtils.clear(0.227f, 0.643f, 0.239f, 1f);

        sceneView.render(gameModel.getDrawables(), gameModel.getFog());
        pheromoneView.render();
        hudView.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        viewportListener.resize(width, height);
        hudView.resize(width, height);
    }

    @Override
    public void dispose() {
        sceneView.dispose();
        pheromoneView.dispose();
        hudView.dispose();
        uiFactory.dispose();
        textureRegistry.dispose();
    }
}
