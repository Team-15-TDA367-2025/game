package se.chalmers.tda367.team15.game.model.managers;

import java.util.Random;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.EnemyFactory;
import se.chalmers.tda367.team15.game.model.entity.enemy.Termite;
import se.chalmers.tda367.team15.game.model.interfaces.observers.TimeObserver;
import se.chalmers.tda367.team15.game.model.interfaces.providers.EntityModificationProvider;

/**
 * When night starts as dictated by {@link GameWorld} spawns a new wave of
 * enemies.
 */
public class WaveManager implements TimeObserver {
    private int nightNumber = 0;
    private EnemyFactory enemyFactory;
    private EntityModificationProvider entityModificationProvider;

    public WaveManager(EnemyFactory enemyFactory, EntityModificationProvider entityModificationProvider) {
        this.enemyFactory = enemyFactory;
        this.entityModificationProvider = entityModificationProvider;
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
            Termite termite = enemyFactory.createTermite(scatter(spawnLocation, 15));
            entityModificationProvider.addEntity(termite);
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
