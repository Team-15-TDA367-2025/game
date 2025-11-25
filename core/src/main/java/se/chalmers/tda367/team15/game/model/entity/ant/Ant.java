package se.chalmers.tda367.team15.game.model.entity.ant;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.AntBehavior;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.WanderBehavior;

public class Ant extends Entity {
    private static final float SPEED = 5f;
    
    private Vector2 velocity;
    private AntBehavior behavior;

    public Ant(Vector2 position) {
        super(position, "libgdx");
        this.behavior = new WanderBehavior();
        pickRandomDirection();
    }

    private void pickRandomDirection() {
        float angle = MathUtils.random.nextFloat() * 2 * MathUtils.PI;
        velocity = new Vector2(MathUtils.cos(angle), MathUtils.sin(angle)).nor().scl(SPEED);
    }

    @Override
    public void update(float deltaTime) {
        // Update with no system awareness
        behavior.update(this, null, deltaTime);
    }

    public void update(float deltaTime, PheromoneSystem system) {
        behavior.update(this, system, deltaTime);
    }

    public void setBehavior(AntBehavior behavior) {
        this.behavior = behavior;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity.set(velocity);
    }
    
    public void move(Vector2 delta) {
        position.add(delta);
    }

    public void updateRotation() {
        if (velocity.len2() > 0.1f) {
            rotation = velocity.angleRad();
        }
    }

    public float getSpeed() {
        return SPEED;
    }
    
    public GridPoint2 getGridPosition() {
        return new GridPoint2((int) Math.floor(position.x), (int) Math.floor(position.y));
    }

    @Override
    public Vector2 getSize() {
        return new Vector2(3f, 0.5f); // Adjusted size
    }
}
