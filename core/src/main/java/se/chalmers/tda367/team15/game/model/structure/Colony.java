package se.chalmers.tda367.team15.game.model.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.CanBeAttacked;
import se.chalmers.tda367.team15.game.model.DestructionListener;
import se.chalmers.tda367.team15.game.model.EntityDeathObserver;
import se.chalmers.tda367.team15.game.model.TimeCycle;
import se.chalmers.tda367.team15.game.model.egg.EggHatchListener;
import se.chalmers.tda367.team15.game.model.egg.EggManager;
import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.Inventory;
import se.chalmers.tda367.team15.game.model.faction.Faction;
import se.chalmers.tda367.team15.game.model.interfaces.TimeObserver;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;

public class Colony extends Structure implements CanBeAttacked, EntityDeathObserver, EggHatchListener, TimeObserver {
    private List<Ant> ants;
    private Inventory inventory;
    private EggManager eggManager;
    private PheromoneSystem pheromoneSystem;
    private float health;
    private float MAX_HEALTH = 60;

    public Colony(GridPoint2 position, PheromoneSystem pheromoneSystem) {
        super(position, "colony", 4);
        this.pheromoneSystem = pheromoneSystem;
        this.ants = new ArrayList<>();
        this.health = MAX_HEALTH;
        faction = Faction.DEMOCRATIC_REPUBLIC_OF_ANTS;
        this.inventory = new Inventory(1000); // test value for now
        this.eggManager = new EggManager();
        this.eggManager.addListener(this);
        // Register to receive ant death notifications
        DestructionListener.getInstance().addEntityDeathObserver(this);
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

    @Override
    public void onEggHatch(AntType type) {
        // Get spawn position (Colony location)
        Vector2 spawnPosition = getPosition();

        // Create the ant directly using AntType
        Ant newAnt = new Ant(spawnPosition, pheromoneSystem, type);

        // Add the ant to the colony
        addAnt(newAnt);
    }

    @Override
    public void onDayStart(TimeCycle timeCycle) {
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
