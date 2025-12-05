package se.chalmers.tda367.team15.game;

import com.badlogic.gdx.Game;

import se.chalmers.tda367.team15.game.screens.StartScreen;

public class Main extends Game {

    @Override
    public void create() {
        setScreen(new StartScreen(this));
    }
}
