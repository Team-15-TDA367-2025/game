package se.chalmers.tda367.team15.game.model.fog;

import com.badlogic.gdx.math.GridPoint2;

public interface FogProvider {
    GridPoint2 getSize();
    boolean isDiscovered(GridPoint2 pos);
}
