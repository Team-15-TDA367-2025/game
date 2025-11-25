package se.chalmers.tda367.team15.game.model;
import se.chalmers.tda367.team15.game.model.entity.Ant;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

public class Colony implements Drawable {
    private List<Ant> ants;
    private Vector2 position;

    public Colony(float x, float y) {
        this.ants = new ArrayList<>();
        this.position = new Vector2(x, y);
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
    public String getTextureName(){
        return "AntColony";
    }

    /** The position in world units */
    public Vector2 getPosition() {
        return position.cpy();
    }

    /** The rotation in radians counter-clockwise */
    public float getRotation() {
        return 0f;
    }   

    /** The size of the object in world units. Defaults to 1x1. */
    public Vector2 getSize() {
        return new Vector2(10f, 10f);
    }
}

