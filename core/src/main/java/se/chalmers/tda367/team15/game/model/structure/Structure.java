package se.chalmers.tda367.team15.game.model.structure;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.Drawable;

public class Structure implements Drawable {
    private GridPoint2 position;
    private String textureName;
    private int radius;

    public Structure(GridPoint2 position, String textureName, int radius) {
        this.position = position;
        this.textureName = textureName;
        this.radius = radius;
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
        return new Vector2(radius * 2, radius * 2);
    }
}
