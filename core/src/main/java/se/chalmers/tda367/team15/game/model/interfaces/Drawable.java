package se.chalmers.tda367.team15.game.model.interfaces;

import com.badlogic.gdx.math.Vector2;

public interface Drawable {
    /** The name of the texture file to use for the object */
    String getTextureName();

    /** The position in world units */
    Vector2 getPosition();

    /** The rotation in radians counter-clockwise */
    default float getRotation() {
        return 0f;
    }

    /** The size of the object in tiles (1 = 1 tile). Defaults to 1x1 tile. */
    default Vector2 getSize() {
        return new Vector2(1f, 1f);
    }
}
