package se.chalmers.tda367.team15.game.model.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Ant extends Entity {
    private Vector2 velocity;
    public Ant(Vector2 position) {
        super(position, "libgdx");
        float randomAngle = MathUtils.random.nextFloat() * 2 * MathUtils.PI;
        velocity = new Vector2(MathUtils.cos(randomAngle), MathUtils.sin(randomAngle));
    }

    @Override
    public void update(float deltaTime) {
        this.position = this.position.add(velocity.cpy().scl(deltaTime));
    }

    @Override
    public Vector2 getSize() {
        // The placeholder texture is 6:1 aspect ratio...
        return new Vector2(6f, 1f).scl(0.5f);
    }
}
