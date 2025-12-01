package se.chalmers.tda367.team15.game.model.structure.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.GridPoint2;

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

    private Map<GridPoint2, List<Resource>> resourceGrid;

    public ResourceSystem() {
        this.resourceGrid = new HashMap<>();
    }

    public void update(Colony colony, List<Entity> entities) {
        List<Ant> ants = filterAnts(entities);

        handleResourcePickup(ants);
        handleResourceDeposit(ants, colony);
    }

    public void addResource(Resource resource) {
        GridPoint2 pos = resource.getGridPosition();
        resourceGrid.computeIfAbsent(pos, k -> new ArrayList<>()).add(resource);
    }

    public void removeResource(Resource resource) {
        GridPoint2 pos = resource.getGridPosition();
        List<Resource> cellResources = resourceGrid.get(pos);
        if (cellResources != null) {
            cellResources.remove(resource);
            if (cellResources.isEmpty()) {
                resourceGrid.remove(pos);
            }
        }
    }

    private void handleResourcePickup(List<Ant> ants) {
        for (Ant ant : ants) {
            if (ant.getInventory().isFull()) {
                continue;
            }

            GridPoint2 antGrid = ant.getGridPosition();
            boolean pickedUp = false;

            for (int dx = -PICKUP_RADIUS; dx <= PICKUP_RADIUS && !pickedUp; dx++) {
                for (int dy = -PICKUP_RADIUS; dy <= PICKUP_RADIUS && !pickedUp; dy++) {

                    GridPoint2 checkCell = new GridPoint2(antGrid.x + dx, antGrid.y + dy);
                    List<Resource> nearbyResources = resourceGrid.get(checkCell);

                    if (nearbyResources != null && !nearbyResources.isEmpty()) {
                        for (Resource resource : new ArrayList<>(nearbyResources)) {
                            if (tryPickupResource(ant, resource)) {
                                if (isResourceDepleted(resource)) {
                                    removeResource(resource);
                                }
                                pickedUp = true;
                                break;
                            }
                        }
                    }
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

            GridPoint2 antGrid = ant.getGridPosition();
            int distance = Math.abs(antGrid.x - colonyGrid.x) +
                    Math.abs(antGrid.y - colonyGrid.y);

            if (distance <= DEPOSIT_RADIUS) {
                ant.leaveResources(colony);
            }
        }
    }

    private boolean tryPickupResource(Ant ant, Resource resource) {

        int amountToPickup = Math.min(resource.getAmount(), ant.getInventory().getRemainingCapacity());

        if (amountToPickup > 0 && ant.getInventory().addResource(resource.getType(), amountToPickup)) {
            resource.setAmount(resource.getAmount() - amountToPickup);
            return true;
        }

        return false;
    }

    public boolean isResourceDepleted(Resource resource) {
        return resource.getAmount() <= 0;
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