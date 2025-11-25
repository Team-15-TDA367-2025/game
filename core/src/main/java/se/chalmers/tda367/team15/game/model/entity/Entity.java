package se.chalmers.tda367.team15.game.model.entity;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.Drawable;

public abstract class Entity implements Drawable {
    protected Vector2 position;
    protected float rotation;
    private String textureName;

    protected Vector2 velocity;

    public Entity(Vector2 position, String textureName) {
        this.position = position;
        this.textureName = textureName;
        this.rotation = 0f;
        this.velocity = new Vector2(0f, 0f);
    }

    public void update(float deltaTime) {
        position.add(velocity.cpy().scl(deltaTime));
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

    public Vector2 getVelocity() {
        return velocity.cpy();
    }

    public void setVelocity(Vector2 vel) {
        velocity.set(vel);
    }

    public void moveBy(Vector2 delta) {
        position.add(delta);
    }

    public void moveTo(Vector2 pos) {
        position.set(pos);
    }
}
