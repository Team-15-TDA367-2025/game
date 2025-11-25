package se.chalmers.tda367.team15.game;

import java.util.ArrayList;
import java.util.List;

public class Colony {
    private List<Ant> ants;
    private float x, y;

    public Colony(float x, float y) {
        this.ants = new ArrayList<>();
        this.x = x;
        this.y = y;
    }

    public int getCount() {
        return ants.size();
    }

    public void addAnt(Ant ant) {
        ants.add(ant);
    }

    public void removeAnt(Ant ant) {
        ants.remove(ant);
    }

}
