package se.chalmers.tda367.team15.game.model.objects;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.tda367.team15.game.model.entities.Ant;

public class Colony extends Object {
    private List<Ant> ants;
    private int size;

    public Colony(int x, int y) {
        super(x, y);
        this.ants = new ArrayList<>();
        this.size = 0;
    }

    public void addAnt(Ant ant) {
        ants.add(ant);
        size++;
    }

    public List<Ant> getAnts() {
        return ants;
    }

    public int getSize() {
        return size;
    }

    public void removeAnt(Ant ant) {
        if (ants.remove(ant)) {
            size--;
        }
    }

}
