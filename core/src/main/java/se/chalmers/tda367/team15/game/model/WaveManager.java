package se.chalmers.tda367.team15.game.model;

import java.util.Random;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.interfaces.TimeObserver;

/**
 * When night starts as dictated by {@link GameWorld} spawns a new wave of
 * enemies.
 */
public class WaveManager implements TimeObserver {
    private int nightNumber = 0;
    private GameModel gameModel;

    WaveManager(GameWorld gameWorld, GameModel gameModel) {
        this.gameModel = gameModel;
        gameWorld.addTimeObserver(this);
    }

    public int getNightNumber() {
        return nightNumber;
    }

    /**
     * spawns a wave of termites according to a scaling difficulty, the termites
     * will have scattered positions from a random direction.
     */
    private void spawnWave() {

        int nEnemies = nightNumber * 2;

        // spawn location
        Vector2 spawnLocation = scatter(new Vector2(0, 0), 45);
        // spawn enemies
        for (int i = 0; i < nEnemies; i++) {
            gameModel.spawnTermite(scatter(spawnLocation, 15));
        }

    }

    /**
     * Calculates a new vector that is a specified distance away from some origin in
     * a random direction.
     *
     * @param origin   the origin point
     * @param distance the distance
     * @return a randomly scattered vector.
     */
    private Vector2 scatter(Vector2 origin, float distance) {
        Random r = new Random();
        float direction = r.nextFloat() * ((float) Math.PI * 2);
        float x = MathUtils.cos(direction);
        float y = MathUtils.sin(direction);
        Vector2 directionV = new Vector2(x, y);
        return origin.cpy().add(directionV.scl(distance));
    }

    @Override
    public void onNightStart() {
        nightNumber++;
        spawnWave();
    }
}
