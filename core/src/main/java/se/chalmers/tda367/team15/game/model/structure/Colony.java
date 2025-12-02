package se.chalmers.tda367.team15.game.model.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;

public class Colony extends Structure {
    private List<Ant> ants;

    public Colony(GridPoint2 position) {
        super(position, "colony", 2);
        this.ants = new ArrayList<>();
    }

    public void addAnt(Ant ant) {
        ants.add(ant);
    }

    public void removeAnt(Ant ant) {
        ants.remove(ant);
    }

    @Override
    public void update(float deltaTime) {
        for (Ant ant : ants) {
            ant.update(deltaTime);
        }
    }

    @Override
    public Collection<Entity> getSubEntities() {
        return Collections.unmodifiableCollection(ants);
    }
}
