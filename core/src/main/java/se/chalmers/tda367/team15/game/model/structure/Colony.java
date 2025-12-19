package se.chalmers.tda367.team15.game.model.structure;

import java.util.List;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.Inventory;
import se.chalmers.tda367.team15.game.model.faction.Faction;
import se.chalmers.tda367.team15.game.model.interfaces.ColonyDataProvider;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.interfaces.TimeObserver;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;

// TODO - Antigravity: SRP violation - implements 5 interfaces, consider extracting ColonyResourceManager, ColonyEggHandler, ColonyCombat
public class Colony extends Structure
        implements Home, TimeObserver, ColonyDataProvider {
    private Inventory inventory;
    private Faction faction;
    private final EntityQuery entityQuery;
    private boolean isDead = false;

    public Colony(GridPoint2 position, EntityQuery entityQuery, int initialFood) {
        super(position, "colony", 4);
        this.inventory = new Inventory(1000000); // test value for now
        this.inventory.addResource(ResourceType.FOOD, initialFood);
        this.entityQuery = entityQuery;
        this.faction = Faction.DEMOCRATIC_REPUBLIC_OF_ANTS;
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
        if (!inventory.addResource(ResourceType.FOOD, -amount)) {
            isDead = true;
        }
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

    public boolean getIsDead() {
        return isDead;
    }
}
