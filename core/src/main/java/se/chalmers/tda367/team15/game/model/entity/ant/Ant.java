package se.chalmers.tda367.team15.game.model.entity.ant;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.VisionProvider;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.AntBehavior;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.WanderBehavior;

public class Ant extends Entity implements VisionProvider {
    private static final float SPEED = 2f;
    private final int visionRadius = 4;

    private AntBehavior behavior;
    private PheromoneSystem system;

    public Ant(Vector2 position, PheromoneSystem system) {
        super(position, "ant");
        this.behavior = new WanderBehavior(this);
        this.system = system;
        pickRandomDirection();
    }

    private void pickRandomDirection() {
        float angle = MathUtils.random.nextFloat() * 2 * MathUtils.PI;
        velocity = new Vector2(MathUtils.cos(angle), MathUtils.sin(angle)).nor().scl(SPEED);
    }

    @Override
    public void update(float deltaTime) {
        behavior.update(system, deltaTime);
        super.update(deltaTime);
        updateRotation();
    }

    public void setBehavior(AntBehavior behavior) {
        this.behavior = behavior;
    }

    public void updateRotation() {
        if (getVelocity().len2() > 0.1f) {
            rotation = getVelocity().angleRad() - MathUtils.PI / 2f;
        }
    }

    public float getSpeed() {
        return SPEED;
    }

    public GridPoint2 getGridPosition() {
        PheromoneGridConverter converter = system.getConverter();
        return converter.worldToPheromoneGrid(position);
    }

    public PheromoneSystem getSystem() {
        return system;
    }

    @Override
    public Vector2 getSize() {
        return new Vector2(1f, 1.5f); // 1 tile wide, 1.5 tiles tall
    }

    @Override
    public int getVisionRadius() {
        return visionRadius;
    }

}
