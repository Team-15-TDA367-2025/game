package se.chalmers.tda367.team15.game.model.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.interfaces.GameObject;
import se.chalmers.tda367.team15.game.model.interfaces.HasPosition;
import se.chalmers.tda367.team15.game.model.interfaces.MovementStrategy;
import se.chalmers.tda367.team15.game.model.interfaces.observers.SimulationObserver;

public abstract class Entity implements GameObject, SimulationObserver, HasPosition {
    protected Vector2 position;
    protected float rotation;
    protected Vector2 velocity;
    private MovementStrategy movementStrategy;

    // TODO reduce amount of parameters clients need to handle
    // Some kind of entity factory might help reduce the amount of parameters
    // GameWorld is useful because it gives entities awareness of the world around
    // them- singleton???

    public Entity(Vector2 position) {
        this.position = position;
        this.rotation = 0f;
        this.velocity = new Vector2(0f, 0f);
    }

    public void setMovementStrategy(MovementStrategy movementStrategy) {
        this.movementStrategy = movementStrategy;
    }

    protected void handleCollision() {
        velocity = new Vector2(0, 0);
    }

    @Override
    public void update(float deltaTime) {
        updateRotation();
        Vector2 velocityStep = velocity.cpy().scl(deltaTime);
        Vector2 nextPosition = position.cpy().add(velocityStep);

        // If no strategy is set, we assume free movement (or you could assume strict)
        if (movementStrategy != null && !movementStrategy.canMoveTo(nextPosition)) {
            handleCollision();
            return;
        }
        position.set(nextPosition);

    }

    @Override
    public Vector2 getPosition() {
        return position.cpy();
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    private void updateRotation() {
        if (getVelocity().len2() > 0.1f) {
            rotation = getVelocity().angleRad() - MathUtils.PI / 2f;
        }
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
