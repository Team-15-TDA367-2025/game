package se.chalmers.tda367.team15.game.model.entity.ant;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.DestructionListener;
import se.chalmers.tda367.team15.game.model.GameWorld;
import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.AntBehavior;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.WanderBehavior;
import se.chalmers.tda367.team15.game.model.faction.Faction;
import se.chalmers.tda367.team15.game.model.interfaces.CanBeAttacked;
import se.chalmers.tda367.team15.game.model.interfaces.VisionProvider;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.structure.Colony;

public class Ant extends Entity implements VisionProvider, CanBeAttacked {
    private final int visionRadius = 4;
    protected Faction faction;
    private final int hunger;

    // Stats from AntType
    private final float speed;
    private final String baseTextureName;
    private GameWorld gameWorld;
    private AntBehavior behavior;
    private PheromoneSystem system;

    private float health;
    private Inventory inventory;

    public Ant(Vector2 position, PheromoneSystem system, AntType type, GameWorld gameWorld) {
        super(position, type.textureName());
        this.gameWorld = gameWorld;
        this.behavior = new WanderBehavior(this, gameWorld);
        this.system = system;
        this.hunger = 2; // test value

        // Initialize from AntType
        this.speed = type.moveSpeed();
        this.health = type.maxHealth();
        this.inventory = new Inventory(type.carryCapacity());
        this.baseTextureName = type.textureName();

        pickRandomDirection();
        this.faction = Faction.DEMOCRATIC_REPUBLIC_OF_ANTS;
        setMovementStrategy(new AntMovementStrategy(gameWorld.getWorldMap()));
    }

    private void pickRandomDirection() {
        float angle = MathUtils.random.nextFloat() * 2 * MathUtils.PI;
        velocity = new Vector2(MathUtils.cos(angle), MathUtils.sin(angle)).nor().scl(speed);
    }

    @Override
    public void handleCollision() {
        pickRandomDirection();
    }

    @Override
    public void update(float deltaTime) {
        updateBehavior(deltaTime);
        super.update(deltaTime);
        updateTexture();
    }

    public void updateBehavior(float deltaTime) {
        behavior.update(system, deltaTime);
    }

    private void updateTexture() {
        if (inventory.isEmpty()) {
            setTextureName(baseTextureName);
        } else {
            // TODO: This should be a more generic solution
            setTextureName("resource");
        }
    }

    public void setBehavior(AntBehavior behavior) {
        this.behavior = behavior;
    }

    public float getSpeed() {
        return speed;
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

    public int getHunger() {
        return hunger;
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

    public GameWorld getGameWorld() {
        return gameWorld;
    }
}
