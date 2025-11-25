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
    
    private AntBehavior behavior;
    private PheromoneSystem system;

    public Ant(Vector2 position, PheromoneSystem system) {
        super(position, "libgdx");
        this.behavior = new WanderBehavior();
        this.system = system;
        pickRandomDirection();
    }

    private void pickRandomDirection() {
        float angle = MathUtils.random.nextFloat() * 2 * MathUtils.PI;
        velocity = new Vector2(MathUtils.cos(angle), MathUtils.sin(angle)).nor().scl(SPEED);
    }

    @Override
    public void update(float deltaTime) {
        behavior.update(this, system, deltaTime);
        super.update(deltaTime);
        updateRotation();
    }

    public void setBehavior(AntBehavior behavior) {
        this.behavior = behavior;
    }

    public void updateRotation() {
        if (getVelocity().len2() > 0.1f) {
            rotation = getVelocity().angleRad();
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
