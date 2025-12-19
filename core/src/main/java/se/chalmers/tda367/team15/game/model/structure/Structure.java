package se.chalmers.tda367.team15.game.model.structure;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.faction.Faction;
import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.interfaces.HasPosition;
import se.chalmers.tda367.team15.game.model.interfaces.SimulationObserver;

// A structure is a static object in the game world, fixed to the grid.
public abstract class Structure implements Drawable, SimulationObserver, HasPosition {
    private GridPoint2 position;
    private String textureName;
    private int size;
    Faction faction;

    public Structure(GridPoint2 position, String textureName, int size) {
        this.position = position;
        this.textureName = textureName;
        this.size = size;
    }

    public GridPoint2 getGridPosition() {
        return position;
    }

    @Override
    public Vector2 getPosition() {
        return new Vector2(position.x, position.y);
    }

    @Override
    public String getTextureName() {
        return textureName;
    }

    @Override
    public Vector2 getSize() {
        return new Vector2(size, size);
    }

    public Faction getFaction() {
        return faction;
    }
}
