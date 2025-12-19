package se.chalmers.tda367.team15.game.controller;

import se.chalmers.tda367.team15.game.model.GameModel;
import se.chalmers.tda367.team15.game.view.ui.SpeedControlsListener;

public class SpeedController implements SpeedControlsListener {
    private final GameModel gameModel;

    public SpeedController(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    @Override
    public void onFastSpeed() {
        gameModel.setTimeFast();
    }

    @Override
    public void onNormalSpeed() {
        gameModel.setTimeNormal();
    }

    @Override
    public void onPause() {
        gameModel.setTimePaused();
    }
}
