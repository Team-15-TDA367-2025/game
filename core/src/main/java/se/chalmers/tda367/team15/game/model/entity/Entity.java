package se.chalmers.tda367.team15.game.model.entity;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.GameWorld;
import se.chalmers.tda367.team15.game.model.faction.Faction;
import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.interfaces.Updatable;

public abstract class Entity implements Drawable, Updatable {
    protected Vector2 position;
    protected float rotation;
    private String textureName;
    protected Vector2 velocity;
    protected Faction faction;
    private GameWorld gameWorld;

    // TODO reduce amount of parameters clients need to handle
    // Some kind of entity factory might help reduce the amount of parameters
    // GameWorld is useful because it gives entities awareness of the world around them

    public Entity(Vector2 position, String textureName, GameWorld gameWorld) {
        this.position = position;
        this.textureName = textureName;
        this.rotation = 0f;
        this.velocity = new Vector2(0f, 0f);
        this.gameWorld = gameWorld;
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
    // It is the responsibility of gameWorld to not expose internals.
    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public Faction getFaction(){
        return faction;
    }
}
