package se.chalmers.tda367.team15.game.model.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.interfaces.TimeObserver;
import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.interfaces.CanBeAttacked;
import se.chalmers.tda367.team15.game.model.DestructionListener;
import se.chalmers.tda367.team15.game.model.interfaces.EntityDeathObserver;
import se.chalmers.tda367.team15.game.model.TimeCycle;
import se.chalmers.tda367.team15.game.model.interfaces.EntityDeathObserver;
import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.faction.Faction;
import se.chalmers.tda367.team15.game.model.entity.ant.Inventory;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;

public class Colony extends Structure implements CanBeAttacked, EntityDeathObserver, TimeObserver {
    private List<Ant> ants;
    private Inventory inventory;

    private float health;
    private float MAX_HEALTH = 600;

    public Colony(GridPoint2 position) {
        super(position, "colony", 2);
        this.ants = new ArrayList<>();
        this.health = MAX_HEALTH;
        faction = Faction.DEMOCRATIC_REPUBLIC_OF_ANTS;
        this.inventory = new Inventory(1000); // test value for now
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
