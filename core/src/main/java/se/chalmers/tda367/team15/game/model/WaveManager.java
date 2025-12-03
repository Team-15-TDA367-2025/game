package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import se.chalmers.tda367.team15.game.model.entity.Termite.Termite;
import se.chalmers.tda367.team15.game.model.interfaces.TimeObserver;

import java.util.Random;

public class WaveManager implements TimeObserver {
    private int nightNumber = 0;
    private int termiteDifficultyCost = 1;

    WaveManager() {

    }

    public int getNightNumber() {
        return nightNumber;
    }

    void spawnWave() {
        //determine budget
        int nightBudget = nightNumber * 3;

        // determine # enemies
        int nEnemies = nightBudget / termiteDifficultyCost;

        // spawn location
        Vector2 spawnLocation = scatter(new Vector2(0,0),0);
        // spawn enemies
        for(int i = 0 ; i < nEnemies; i++) {
            Termite termite = new Termite(scatter(spawnLocation,20));
            GameWorld.getInstance().addEntity(termite);
        }

    }

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

    }

    @Override
    public void onDayStart(TimeCycle timeCycle) {

    }

    @Override
    public void onNightStart(TimeCycle timeCycle) {
        nightNumber++;
        spawnWave();
    }
}
