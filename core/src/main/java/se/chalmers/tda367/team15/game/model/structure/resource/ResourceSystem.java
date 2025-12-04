package se.chalmers.tda367.team15.game.model.structure.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.structure.Colony;

/**
 * Manages resource interactions using persistent spatial grid.
 * Grid is maintained internally and only modified when resources change.
 */
public class ResourceSystem {
    private static final int PICKUP_RADIUS = 2;
    private static final int DEPOSIT_RADIUS = 2;

    private Map<GridPoint2, Resource> resourceGrid;

    public ResourceSystem() {
        this.resourceGrid = new HashMap<>();
    }

    public void update(Colony colony, List<Entity> entities, List<Resource> resources) {
        List<Ant> ants = filterAnts(entities);
        handleResourcePickup(ants);
        handleResourceDeposit(ants, colony);
        resources.removeIf(resource -> resource.getAmount() <= 0);
    }

    public void addResource(Resource resource) {
        GridPoint2 pos = resource.getGridPosition();
        resourceGrid.put(pos, resource);
    }

    public void removeResource(Resource resource) {
        GridPoint2 pos = resource.getGridPosition();
        Resource cellResource = resourceGrid.get(pos);
        if (cellResource != null && cellResource.equals(resource)) {
            resourceGrid.remove(pos);
        }
    }

    private void handleResourcePickup(List<Ant> ants) {
        for (Ant ant : ants) {
            if (!ant.getInventory().isFull()) {
                tryPickupNearbyResource(ant);
            }
        }
    }

    private void tryPickupNearbyResource(Ant ant) {
        GridPoint2 antGrid = getAntGridPosition(ant);
        GridPoint2 checkCell = new GridPoint2();

        for (int dx = -PICKUP_RADIUS; dx <= PICKUP_RADIUS; dx++) {
            for (int dy = -PICKUP_RADIUS; dy <= PICKUP_RADIUS; dy++) {
                checkCell.set(antGrid.x + dx, antGrid.y + dy);
                Resource resource = resourceGrid.get(checkCell);
                if (resource != null && tryPickupResource(ant, resource)) {
                    return;
                }
            }
        }
    }

    private void handleResourceDeposit(List<Ant> ants, Colony colony) {
        if (colony == null) {
            return;
        }

        GridPoint2 colonyGrid = colony.getGridPosition();

        for (Ant ant : ants) {
            if (ant.getInventory().isEmpty()) {
                continue;
            }

            GridPoint2 antGrid = getAntGridPosition(ant);
            int distance = Math.abs(antGrid.x - colonyGrid.x) +
                    Math.abs(antGrid.y - colonyGrid.y);

            if (distance <= DEPOSIT_RADIUS) {
                ant.leaveResources(colony);
            }
        }
    }

    private GridPoint2 getAntGridPosition(Ant ant) {
        Vector2 pos = ant.getPosition();
        return new GridPoint2(Math.round(pos.x), Math.round(pos.y));
    }

    private boolean tryPickupResource(Ant ant, Resource resource) {

        int amountToPickup = Math.min(resource.getAmount(), ant.getInventory().getRemainingCapacity());

        if (amountToPickup > 0 && ant.getInventory().addResource(resource.getType(), amountToPickup)) {
            resource.setAmount(resource.getAmount() - amountToPickup);
            if (resource.getAmount() <= 0) {
                removeResource(resource);
            }
            return true;
        }

        return false;
    }

    private List<Ant> filterAnts(List<Entity> entities) {
        List<Ant> ants = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity instanceof Ant) {
                ants.add((Ant) entity);
            }
        }
        return ants;
    }
}