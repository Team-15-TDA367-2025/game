package se.chalmers.tda367.team15.game;

import com.badlogic.gdx.Game;

import se.chalmers.tda367.team15.game.screens.StartScreen;
import se.chalmers.tda367.team15.game.screens.game.GameFactory;

public class Main extends Game {
    private final GameFactory gameFactory;

    public Main(GameConfiguration gameConfiguration) {
        this.gameFactory = new GameFactory(gameConfiguration);
    }

    @Override
    public void create() {
        setScreen(new StartScreen(this, gameFactory));
    }
}
