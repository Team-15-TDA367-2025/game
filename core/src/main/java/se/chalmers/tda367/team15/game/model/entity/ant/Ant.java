package se.chalmers.tda367.team15.game.model.entity.ant;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.DestructionListener;
import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.*;
import se.chalmers.tda367.team15.game.model.faction.Faction;
import se.chalmers.tda367.team15.game.model.interfaces.*;
import se.chalmers.tda367.team15.game.model.managers.StructureManager;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.world.MapProvider;

import java.util.HashMap;

public class Ant extends Entity implements VisionProvider, CanAttack {
    AntType type;
    private final int visionRadius = 8;
    protected final Faction faction;
    private final Home home;
    private final int hunger;

    // Stats from AntType
    private final float speed;
    private final String baseTextureName;
    private final Inventory inventory;
    private final DestructionListener destructionListener;
    private final PheromoneSystem system;


    private EntityQuery entityQuery;
    private StructureManager structureManager;
    HashMap<AttackCategory, Integer> targetPriority;

    private GeneralizedBehaviour behavior;
    private float health;

    public Ant(Vector2 position, PheromoneSystem system, AntType type, MapProvider map, Home home, EntityQuery entityQuery, StructureManager structureManager, HashMap<AttackCategory, Integer> targetPriority, DestructionListener destructionListener) {
        super(position, type.textureName());
        this.type = type;
        this.behavior = new WanderBehavior(this, home, entityQuery);
        this.system = system;
        this.hunger = 2; // test value
        this.home = home;
        this.entityQuery = entityQuery;
        this.structureManager = structureManager;
        this.targetPriority= targetPriority;

        // Initialize from AntType
        this.speed = type.moveSpeed();
        this.health = type.maxHealth();
        this.inventory = new Inventory(type.carryCapacity());
        this.baseTextureName = type.textureName();

        pickRandomDirection();
        this.faction = Faction.DEMOCRATIC_REPUBLIC_OF_ANTS;
        setMovementStrategy(new AntMovementStrategy(map));
        this.destructionListener = destructionListener;
    }

    public void pickRandomDirection() {
        float angle = MathUtils.random.nextFloat() * 2 * MathUtils.PI;
        velocity = new Vector2(MathUtils.cos(angle), MathUtils.sin(angle)).nor().scl(speed);
    }

    // TODO
    @Override
    public void handleCollision() {
        behavior.handleCollision();
    }

    @Override
    public void update(float deltaTime) {
        updateBehavior();
        super.update(deltaTime);
        updateTexture();
    }

    public void updateBehavior() {
        behavior.update(system);
    }

    private void updateTexture() {
        if (inventory.isEmpty()) {
            setTextureName(baseTextureName);
        } else {
            // TODO: This should be a more generic solution
            setTextureName("resource");
        }
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
            updateTexture();
        }
        return deposited;
    }

    @Override
    public Vector2 getSize() {
        return new Vector2(1f, 1.5f); // 1 tile wide, 1.5 tiles tall
    }

    @Override
    public Entity getEntity() {
        return null;
    }

    @Override
    public int getVisionRadius() {
        return visionRadius;
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
        behavior = new WanderBehavior(this,home,entityQuery);
    }
    public void setFollowTrailBehaviour(){
        behavior = new FollowTrailBehavior(home,entityQuery,this);
    }
    public void setAttackBehaviour() {
        behavior = new AntAttackBehavior(this,entityQuery,structureManager,targetPriority);
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
