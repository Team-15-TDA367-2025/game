package se.chalmers.tda367.team15.game.screens.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

import se.chalmers.tda367.team15.game.controller.CameraController;
import se.chalmers.tda367.team15.game.controller.HudController;
import se.chalmers.tda367.team15.game.model.GameEndReason;
import se.chalmers.tda367.team15.game.model.GameModel;
import se.chalmers.tda367.team15.game.model.GameStats;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;
import se.chalmers.tda367.team15.game.screens.EndScreen;
import se.chalmers.tda367.team15.game.view.TextureRegistry;
import se.chalmers.tda367.team15.game.view.camera.CameraView;
import se.chalmers.tda367.team15.game.view.camera.ViewportListener;
import se.chalmers.tda367.team15.game.view.renderers.PheromoneRenderer;
import se.chalmers.tda367.team15.game.view.renderers.WorldRenderer;
import se.chalmers.tda367.team15.game.view.ui.HudView;
import se.chalmers.tda367.team15.game.view.ui.UiSkin;

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
    private final UiSkin uiFactory;
    private final ViewportListener viewportListener;

    // Controllers
    private final CameraController cameraController;
    private final HudController hudController;
    private final GameFactory gameFactory;

    private final Game game;

    public GameScreen(
            GameFactory gameFactory,
            Game game,
            GameModel gameModel,
            CameraView cameraView,
            WorldRenderer sceneView,
            PheromoneRenderer pheromoneView,
            HudView hudView,
            TextureRegistry textureRegistry,
            UiSkin uiFactory,
            ViewportListener viewportListener,
            CameraController cameraController,
            HudController hudController) {
        this.game = game;
        this.gameFactory = gameFactory;
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

    private GameEndReason gameHasEnded() {
        if (gameModel.getTotalAnts() == 0) {
            return GameEndReason.ALL_ANTS_DEAD;
        }
        if (gameModel.getColonyDataProvider().getTotalResources(ResourceType.FOOD) < 0) {
            return GameEndReason.STARVATION;
        }
        return GameEndReason.STILL_PLAYING;
    }

    @Override
    public void render(float delta) {
        // Update
        cameraController.update(delta);
        cameraView.updateCamera();
        gameModel.update();
        hudController.update(delta);

        GameEndReason endReason = gameHasEnded();
        if (endReason != GameEndReason.STILL_PLAYING) {
            GameStats gameStats = new GameStats(gameModel.getTimeProvider().getGameTime().totalDays()); // TODO long
                                                                                                        // line
            gameStats.saveIfNewHighScore();
            game.setScreen(new EndScreen(game, endReason, gameFactory));
        }

        // Render
        ScreenUtils.clear(0.227f, 0.643f, 0.239f, 1f);

        sceneView.render(gameModel.getDrawables());
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
