package se.chalmers.tda367.team15.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

public class InputManager {
    private final InputMultiplexer multiplexer;

    public InputManager() {
        this.multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);
    }

    public void addProcessor(InputProcessor processor) {
        multiplexer.addProcessor(processor);
    }

    public void removeProcessor(InputProcessor processor) {
        multiplexer.removeProcessor(processor);
    }
}
