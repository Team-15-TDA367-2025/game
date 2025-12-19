package se.chalmers.tda367.team15.game.model.entity.ant;

import java.util.HashMap;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.DestructionListener;
import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.AntAttackBehavior;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.FollowTrailBehavior;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.GeneralizedBehaviour;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.WanderBehavior;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.trail.TrailStrategy;
import se.chalmers.tda367.team15.game.model.faction.Faction;
import se.chalmers.tda367.team15.game.model.interfaces.CanAttack;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.interfaces.providers.VisionProvider;
import se.chalmers.tda367.team15.game.model.managers.PheromoneManager;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.world.MapProvider;

public class Ant extends Entity implements VisionProvider, CanAttack {
    AntType type;
    protected final Faction faction;
    private final Home home;
    private float health;
    // Stats from AntType
    private final Inventory inventory;
    private final DestructionListener destructionListener;
    private final PheromoneManager system;

    private final EntityQuery entityQuery;
    HashMap<AttackCategory, Integer> targetPriority;

    private GeneralizedBehaviour behavior;

    public Ant(Vector2 position, PheromoneManager system, AntType type, MapProvider map, Home home,
            EntityQuery entityQuery,
            HashMap<AttackCategory, Integer> targetPriority, DestructionListener destructionListener) {
        super(position);
        this.type = type;
        this.behavior = new WanderBehavior(this, home, entityQuery);
        this.system = system;
        this.home = home;
        this.entityQuery = entityQuery;
        this.targetPriority = targetPriority;
        // Initialize from AntType
        this.inventory = new Inventory(type.carryCapacity());
        this.health = type.maxHealth();
        pickRandomDirection();
        this.faction = Faction.DEMOCRATIC_REPUBLIC_OF_ANTS;
        setMovementStrategy(new AntMovementStrategy(map));
        this.destructionListener = destructionListener;
    }

    public void pickRandomDirection() {
        float angle = MathUtils.random.nextFloat() * 2 * MathUtils.PI;
        velocity = new Vector2(MathUtils.cos(angle), MathUtils.sin(angle)).nor().scl(type.moveSpeed());
    }

    @Override
    public void handleCollision() {
        behavior.handleCollision();
    }

    @Override
    public void update(float deltaTime) {
        updateBehavior();
        super.update(deltaTime);
    }

    public void updateBehavior() {
        behavior.update(system);
    }

    public GridPoint2 getGridPosition() {
        PheromoneGridConverter converter = system.getConverter();
        return converter.worldToPheromoneGrid(position);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getHunger() {
        return type.hunger();
    }

    public AntType getType() {
        return type;
    }

    public boolean leaveResources(Home home) {
        if (inventory.isEmpty()) {
            return false;
        }
        boolean deposited = home.depositResources(inventory);
        if (deposited) {
            inventory.clear();
        }
        return deposited;
    }

    @Override
    public float getSpeed() {
        return type.moveSpeed();
    }

    @Override
    public void setVelocity(Vector2 v) {
        super.setVelocity(v);
    }

    @Override
    public Vector2 getSize() {
        return new Vector2(1f, 1.5f); // 1 tile wide, 1.5 tiles tall
    }

    @Override
    public int getVisionRadius() {
        return type.visionRadius();
    }

    @Override
    public float getAttackDamage() {
        return 2;
    }

    @Override
    public float getAttackRange() {
        return 2;
    }

    @Override
    public int getAttackCoolDownMs() {
        return 1000;
    }

    public Home getHome() {
        return home;
    }

    @Override
    public Faction getFaction() {
        return faction;
    }

    public void setWanderBehaviour() {
        setWanderBehaviour(false);
    }

    /**
     * Switches to wander behavior.
     *
     * @param leftTrail If true, applies a cooldown before re-entering any trail
     */
    public void setWanderBehaviour(boolean leftTrail) {
        behavior = new WanderBehavior(this, home, entityQuery, leftTrail);
    }

    public void setFollowTrailBehaviour() {
        behavior = new FollowTrailBehavior(entityQuery, this, system.getConverter());
    }

    public void setAttackBehaviour() {
        behavior = new AntAttackBehavior(this, entityQuery, targetPriority);
    }

    @Override
    public String getTypeId() {
        return type.id();
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
        destructionListener.notifyEntityDeathObservers(this);
    }

    @Override
    public AttackCategory getAttackCategory() {
        return AttackCategory.WORKER_ANT;
    }
}
