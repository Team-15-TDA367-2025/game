package se.chalmers.tda367.team15.game.model.structure;

import java.util.List;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.Inventory;
import se.chalmers.tda367.team15.game.model.faction.Faction;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.interfaces.observers.TimeObserver;
import se.chalmers.tda367.team15.game.model.interfaces.providers.ColonyDataProvider;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;

// TODO - Antigravity: SRP violation - implements 5 interfaces, consider extracting ColonyResourceManager, ColonyEggHandler, ColonyCombat
public class Colony extends Structure implements Home, TimeObserver, ColonyDataProvider {
    private Inventory inventory;
    private final Faction faction;
    private final EntityQuery entityQuery;

    public Colony(GridPoint2 position, EntityQuery entityQuery, int initialFood) {
        super(position, 4);
        this.faction = Faction.DEMOCRATIC_REPUBLIC_OF_ANTS;
        this.inventory = new Inventory(null); // test value for now
        this.inventory.addResource(ResourceType.FOOD, initialFood);
        this.entityQuery = entityQuery;
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
        return getAnts().stream().mapToInt(Ant::getHunger).sum();
    }

    public void applyConsumption(int amount) {
        inventory.addResource(ResourceType.FOOD, -amount);
    }

    public int getTotalResources(ResourceType type) {
        return inventory.getAmount(type);
    }

    public boolean spendResources(ResourceType type, int amount) {
        return inventory.addResource(type, -amount);
    }

    public void onDayStart() {
        applyConsumption(getConsumption());
    }

    @Override
    public Faction getFaction() {
        return faction;
    }

    @Override
    public List<Ant> getAnts() {
        return entityQuery
                .getEntitiesOfType(Ant.class)
                .stream()
                .filter(ant -> ant.getHome() == this)
                .toList();
    }

    @Override
    public String getTypeId() {
        return "colony";
    }
}
