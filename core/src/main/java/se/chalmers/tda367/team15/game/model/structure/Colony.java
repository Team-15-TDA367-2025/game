package se.chalmers.tda367.team15.game.model.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.*;
import se.chalmers.tda367.team15.game.model.egg.EggHatchObserver;
import se.chalmers.tda367.team15.game.model.egg.EggManager;
import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;
import se.chalmers.tda367.team15.game.model.entity.ant.Inventory;
import se.chalmers.tda367.team15.game.model.faction.Faction;
import se.chalmers.tda367.team15.game.model.interfaces.CanBeAttacked;
import se.chalmers.tda367.team15.game.model.interfaces.EntityDeathObserver;
import se.chalmers.tda367.team15.game.model.interfaces.TimeObserver;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;

public class Colony extends Structure implements CanBeAttacked, EntityDeathObserver, EggHatchObserver, TimeObserver {
    private List<Ant> ants;
    private Inventory inventory;
    private EggManager eggManager;
    private float health;
    private float MAX_HEALTH = 600;
    private AntFactory antFactory;
    private Faction faction;

    public Colony(GridPoint2 position, GameWorld world,TimeCycle timeCycle ,SimulationHandler simulationHandler) {
        super(position, "colony", 4);
        this.ants = new ArrayList<>();
        this.health = MAX_HEALTH;
        faction = Faction.DEMOCRATIC_REPUBLIC_OF_ANTS;
        this.inventory = new Inventory(1000); // test value for now
        this.eggManager = new EggManager(simulationHandler);
        this.eggManager.addObserver(this);

        timeCycle.addTimeObserver(this);
        // Register to receive ant death notifications
        DestructionListener.getInstance().addEntityDeathObserver(this);
        antFactory = new AntFactory(world.getPheromoneSystem(), this, world,simulationHandler);
    }

    public void addAnt(Ant ant) {
        ants.add(ant);
    }

    public void removeAnt(Ant ant) {
        ants.remove(ant);
    }

    public boolean depositResources(Inventory otherInventory) {
        boolean deposited = false;

        for (ResourceType type : ResourceType.values()) {
            int total = otherInventory.getAmount(type);
            if (total > 0) {
                inventory.addResource(type, total);
                deposited = true;
            }
        }
        return deposited;
    }

    public int calculateConsumption() {
        int total = 0;

        for (Ant ant : ants) {
            total += ant.getHunger();
        }
        return total;
    }

    public void applyConsumption(int amount) {
        inventory.addResource(ResourceType.FOOD, -amount);

    }

    public int getTotalResources(ResourceType type) {
        return inventory.getAmount(type);
    }

    /**
     * Gets the egg manager for this colony.
     *
     * @return the egg manager
     */
    public EggManager getEggManager() {
        return eggManager;
    }

    /**
     * Attempts to purchase an egg of the specified type.
     * Checks if the colony has enough resources and deducts them if successful.
     *
     * @param type the type of egg to purchase
     * @return true if the purchase was successful, false otherwise
     */
    public boolean purchaseEgg(AntType type) {
        if (type == null) {
            return false;
        }

        int foodCost = type.foodCost();
        if (getTotalResources(ResourceType.FOOD) < foodCost) {
            return false;
        }

        // Deduct resources
        inventory.addResource(ResourceType.FOOD, -foodCost);
        // Add egg to manager
        eggManager.addEgg(type);
        return true;
    }

    public int getAntCount() {
        return ants.size();
    }

    public void spawnInitialAnts() {
        AntTypeRegistry registry = AntTypeRegistry.getInstance();
        AntType type = registry.get("worker");
        Ant ant = antFactory.createAnt(type);
        addAnt(ant);
    }

    @Override
    public void onEggHatch(AntType type) {
        // Create the ant directly using AntType

        Ant ant = antFactory.createAnt(type);

        // Add the ant to the colony
        addAnt(ant);
    }

    @Override
    public void onDayStart() {
        applyConsumption(calculateConsumption());
    }

    @Override
    public Collection<Entity> getSubEntities() {
        return Collections.unmodifiableCollection(ants);
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
        DestructionListener.getInstance().notifyStructureDeathObservers(this);
    }

    @Override
    public AttackCategory getAttackCategory() {
        return AttackCategory.ANT_COLONY;
    }

    @Override
    public void onEntityDeath(Entity e) {
        // Remove dead ants from our list
        if (e instanceof Ant) {
            ants.remove(e);
        }
    }
}
