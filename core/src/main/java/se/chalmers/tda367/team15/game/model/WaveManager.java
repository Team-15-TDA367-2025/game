package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.Termite.Termite;
import se.chalmers.tda367.team15.game.model.interfaces.TimeObserver;

import java.util.List;
import java.util.Random;

/**
 * When combat phase (night) starts as dictated by {@link TimeCycle} spawns a new wave of enemies.
 */
public class WaveManager implements TimeObserver {
    private int nightNumber = 0;
    private boolean dayIsNext = false;


    WaveManager() {

    }

    public int getNightNumber() {
        return nightNumber;
    }

    /**
     * spawns a wave of termites according to a scaling difficulty, the termites will have scattered positions from a random direction.
     */
    private void spawnWave() {

        List<Entity> e = GameWorld.getInstance().getEntities();
        System.out.println(e.size());

        int nEnemies = nightNumber*2;

        // spawn location
        Vector2 spawnLocation = scatter(new Vector2(0,0),45);
        // spawn enemies
        for(int i = 0 ; i < nEnemies; i++) {
            Termite termite = new Termite(scatter(spawnLocation,15));
            GameWorld.getInstance().addEntity(termite);
        }

    }

    /**
     * Calculates a new vector that is a specified distance away from some origin in a random direction.
     * @param origin the origin point
     * @param distance the distance
     * @return a randomly scattered vector.
     */
    Vector2 scatter(Vector2 origin,float distance) {
        Random r = new Random();
        float direction = r.nextFloat()*((float) Math.PI * 2);
        float x = MathUtils.cos(direction);
        float y = MathUtils.sin(direction);
        Vector2 directionV = new Vector2(x,y);
        return origin.add(directionV.scl(distance));
    }

    @Override
    public void onTimeUpdate(TimeCycle timeCycle) {

        if(dayIsNext) {
            if(timeCycle.getIsDay()) {
               dayIsNext =false;
            }
        }
        else {
            if(!timeCycle.getIsDay()) {
                dayIsNext=true;
                nightNumber++;
                spawnWave();
            }
        }


    }
}
