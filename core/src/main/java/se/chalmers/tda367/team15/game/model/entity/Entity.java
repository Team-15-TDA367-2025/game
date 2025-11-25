package se.chalmers.tda367.team15.game.model.entity;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.Drawable;

public abstract class Entity implements Drawable {
    protected Vector2 position;
    protected float rotation;
    private String textureName;

    public Entity(Vector2 position, String textureName) {
        this.position = position;
        this.textureName = textureName;
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
        return textureName;
    }
}
