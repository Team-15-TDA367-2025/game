package se.chalmers.tda367.team15.game.model.entities;

public abstract class Entity {
    protected float x;
    protected float y;

    public Entity(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setPositionX(float x) {
        this.x = x;
    }

    public void setPositionY(float y) {
        this.y = y;
    }

}