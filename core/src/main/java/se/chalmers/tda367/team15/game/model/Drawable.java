package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.Vector2;

public interface Drawable {
    /** The name of the texture file to use for the object */
    public String getTextureName();

    /** The position in world units */
    public Vector2 getPosition();

    /** The rotation in radians counter-clockwise */
    public float getRotation();

    /** The size of the object in world units. Defaults to 1x1. */
    default Vector2 getSize() {
        return new Vector2(1f, 1f);
    }
}
