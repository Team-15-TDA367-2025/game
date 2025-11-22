package se.chalmers.tda367.team15.game.model.entity;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.IDrawable;

public abstract class Entity implements IDrawable {
    protected Vector2 position;
    protected float rotation;
    private String atlasName;

    public Entity(Vector2 position, String atlasName) {
        this.position = position;
        this.atlasName = atlasName;
        this.rotation = 0f;
    }

    public void update(float deltaTime) {
        // Do nothing by default
    }

    @Override
    public Vector2 getPosition() {
        return position.cpy();
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public String getTextureName() {
        return atlasName;
    }
}
