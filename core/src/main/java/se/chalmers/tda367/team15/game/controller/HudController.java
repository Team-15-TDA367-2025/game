package se.chalmers.tda367.team15.game.controller;

import se.chalmers.tda367.team15.game.view.HudView;

public class HudController {
    private HudView view;
    private int dummyScore = 0;

    private int dayDummy = 0;

    public HudController(HudView view) {
        this.view = view;
    }

    public void update(float delta) {
        dummyScore += 1; // temporary dummy model
        dayDummy = dummyScore % 7;
        view.updateData(dayDummy, dummyScore, dummyScore, dummyScore);
    }
}
