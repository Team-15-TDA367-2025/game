package se.chalmers.tda367.team15.game.model.structure;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.DestructionListener;
import se.chalmers.tda367.team15.game.model.SimulationHandler;
import se.chalmers.tda367.team15.game.model.TimeCycle;
import se.chalmers.tda367.team15.game.model.egg.EggHatchObserver;
import se.chalmers.tda367.team15.game.model.egg.EggManager;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.Inventory;
import se.chalmers.tda367.team15.game.model.faction.Faction;
import se.chalmers.tda367.team15.game.model.interfaces.CanBeAttacked;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.interfaces.TimeObserver;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;

public class Colony extends Structure implements CanBeAttacked, Home, EggHatchObserver, TimeObserver {
    private Inventory inventory;
    private EggManager eggManager;
    private float health;
    private float MAX_HEALTH = 600;
    private Faction faction;
    private final EntityQuery entityQuery;
    private AntHatchListener antHatchListener;

    /**
     * Listener for when ants hatch from eggs.
     * Used to notify external systems (like GameModel) to create and add the ant.
     */
    public interface AntHatchListener {
        void onAntHatch(AntType type);
    }

    public Colony(GridPoint2 position, TimeCycle timeCycle, SimulationHandler simulationHandler, EntityQuery entityQuery) {
        super(position, "colony", 4);
        this.health = MAX_HEALTH;
        this.faction = Faction.DEMOCRATIC_REPUBLIC_OF_ANTS;
        this.inventory = new Inventory(1000); // test value for now
        this.eggManager = new EggManager(simulationHandler);
        this.eggManager.addObserver(this);
        this.entityQuery = entityQuery;

        timeCycle.addTimeObserver(this);
    }

    public void setAntHatchListener(AntHatchListener listener) {
        this.antHatchListener = listener;
    }

    @Override
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
        for (Ant ant : entityQuery.getEntitiesOfType(Ant.class)) {
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

    // TODO: Eggs should not be handled here probably
    public EggManager getEggManager() {
        return eggManager;
    }

    public boolean purchaseEgg(AntType type) {
        if (type == null) {
            return false;
        }

        int foodCost = type.foodCost();
        if (getTotalResources(ResourceType.FOOD) < foodCost) {
            return false;
        }

        inventory.addResource(ResourceType.FOOD, -foodCost);
        eggManager.addEgg(type);
        return true;
    }

    public int getAntCount() {
        return entityQuery.getEntitiesOfType(Ant.class).size();
    }

    @Override
    public void onEggHatch(AntType type) {
        if (antHatchListener != null) {
            antHatchListener.onAntHatch(type);
        }
    }

    @Override
    public void onDayStart() {
        applyConsumption(calculateConsumption());
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
}
