package se.chalmers.tda367.team15.game.model.structure;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.AntFactory;
import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.DestructionListener;
import se.chalmers.tda367.team15.game.model.egg.EggHatchObserver;
import se.chalmers.tda367.team15.game.model.egg.EggManager;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.Inventory;
import se.chalmers.tda367.team15.game.model.faction.Faction;
import se.chalmers.tda367.team15.game.model.interfaces.CanBeAttacked;
import se.chalmers.tda367.team15.game.model.interfaces.ColonyUsageProvider;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.interfaces.TimeObserver;
import se.chalmers.tda367.team15.game.model.managers.EntityManager;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;

public class Colony extends Structure implements Home, EggHatchObserver, TimeObserver, ColonyUsageProvider {
    private Inventory inventory;
    private final EggManager eggManager;
    private Faction faction;
    private final EntityQuery entityQuery;
    private final EntityManager entityManager;
    private final DestructionListener destructionListener;

    public Colony(GridPoint2 position, EntityQuery entityQuery, EggManager eggManager, EntityManager entityManager, DestructionListener destructionListener) {
        super(position, "colony", 4);
        this.faction = Faction.DEMOCRATIC_REPUBLIC_OF_ANTS;
        this.inventory = new Inventory(1000000); // test value for now
        this.eggManager = eggManager;
        this.entityQuery = entityQuery;
        this.entityManager = entityManager;
        this.destructionListener = destructionListener;
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

    @Override
    public int getConsumption() {
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

    @Override
    public void onEggHatch(AntFactory factory, AntType type) {
        // TODO: Kinda violates SRP
        Ant ant = factory.createAnt(this, type);
        this.entityManager.addEntity(ant);
    }

    public void onDayStart() {
        applyConsumption(getConsumption());
    }

    @Override
    public Faction getFaction() {
        return faction;
    }

    @Override
    public int getTotalAnts() {
        // TODO: fix this
        return entityQuery.getEntitiesOfType(Ant.class).size();
    }
}
