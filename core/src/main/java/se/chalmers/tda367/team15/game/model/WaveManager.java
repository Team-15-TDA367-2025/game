package se.chalmers.tda367.team15.game.model;

import java.util.Random;

public class WaveManager {
    private int nightNumber = 0;
    private float nightDifficultyDeviation = 0.15f;

    private int termiteDifficultyCost = 1;

    WaveManager() {

    }

    public int getNightNumber() {
        return nightNumber;
    }

    void spawnWave() {
        //determine budget
        double difficultyMin = nightNumber * 3;
        double difficultyMax = difficultyMin * (1 + nightDifficultyDeviation);



        Random r = new Random();
        double random = difficultyMin + r.nextDouble() * (difficultyMax - difficultyMin);
        int nightBudget = (int) random;




        // Determine amount of waves.
        double waveBudgetMin = (nightBudget*0.15);
        double waveBudgetMax= waveBudgetMin + nightBudget * 0.15;


        double waveBudget = waveBudgetMin + r.nextDouble() * (waveBudgetMax - waveBudgetMin);

        // find colony


        // determine spawn locations

        //

    }

    void scatter() {

    }


}
