package se.chalmers.tda367.team15.game.controller;

import se.chalmers.tda367.team15.game.model.GameModel;
import se.chalmers.tda367.team15.game.model.GameWorld;
import se.chalmers.tda367.team15.game.view.ui.SpeedControlsListener;

public class SpeedController implements SpeedControlsListener {
    private final GameModel gameModel;

    public SpeedController(GameModel gameModel) {
        this.gameModel=gameModel;
    }

    @Override
    public void onFastSpeed() {
        System.out.println("fast");
    }

    @Override
    public void onNormalSpeed() {
        System.out.println("normal");
    }

    @Override
    public void onPause() {
        System.out.println("pause");
    }
}
