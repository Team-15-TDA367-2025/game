package se.chalmers.tda367.team15.game.model.entity.ant;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.CanBeAttacked;
import se.chalmers.tda367.team15.game.model.DestructionListener;
import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.VisionProvider;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.AntBehavior;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.WanderBehavior;
import se.chalmers.tda367.team15.game.model.faction.Faction;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.structure.Colony;


public class Ant extends Entity implements VisionProvider, CanBeAttacked {
    private static final float SPEED = 5f;
    private final float MAX_HEALTH = 6;
    private final int visionRadius = 4;
    Faction faction;
    private AntBehavior behavior;
    private PheromoneSystem system;

    private float health;
    private Inventory inventory;

    public Ant(Vector2 position, PheromoneSystem system, int capacity) {
        super(position, "ant");
        this.behavior = new WanderBehavior(this);
        this.system = system;
        this.inventory = new Inventory(capacity);
        pickRandomDirection();
        this.faction = Faction.DEMOCRATIC_REPUBLIC_OF_ANTS;
        this.health = MAX_HEALTH;
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
        updateTexture();
    }

    private void updateTexture() {
        if (inventory.isEmpty()) {
            setTextureName("ant");
        } else {
            setTextureName("AntCarryingFood");
        }
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

    public Inventory getInventory() {
        return inventory;
    }

    public boolean leaveResources(Colony colony) {
        if (inventory.isEmpty()) {
            return false;
        }
        boolean deposited = colony.depositResources(inventory);
        if (deposited) {
            inventory.clear();
            updateTexture();
        }
        return deposited;
    }

    @Override
    public Vector2 getSize() {
        return new Vector2(1f, 1.5f); // 1 tile wide, 1.5 tiles tall
    }

    @Override
    public int getVisionRadius() {
        return visionRadius;
    }

    @Override
    public Faction getFaction() {
        return faction;
    }

    @Override
    public void takeDamage(float amount) {
        health = Math.max(0f, health - amount);
        if (health == 0f) {
            die();
        }
    }

    @Override
    public void die() {
        DestructionListener.getInstance().notifyEntityDeathObservers(this);
    }

    @Override
    public AttackCategory getAttackCategory() {
        return AttackCategory.WORKER_ANT;
    }
}
