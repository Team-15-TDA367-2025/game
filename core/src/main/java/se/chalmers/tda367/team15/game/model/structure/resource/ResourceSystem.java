package se.chalmers.tda367.team15.game.model.structure.resource;

import java.util.List;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.interfaces.Updatable;
import se.chalmers.tda367.team15.game.model.managers.StructureManager;
import se.chalmers.tda367.team15.game.model.structure.Structure;

/**
 * Manages resource interactions using persistent spatial grid.
 * Grid is maintained internally and only modified when resources change.
 */
public class ResourceSystem implements Updatable {
    private static final int PICKUP_RADIUS = 2;
    private static final int DEPOSIT_RADIUS = 2;
    private EntityQuery entityQuery;
    private StructureManager structureManager;

    public ResourceSystem(EntityQuery entityQuery, StructureManager structureManager) {
        this.entityQuery = entityQuery;
        this.structureManager = structureManager;
    }

    @Override
    public void update(float deltaTime) {

        List<Structure> structures = entityQuery.getEntitiesOfType(Structure.class);

        List<Ant> ants = entityQuery.getEntitiesOfType(Ant.class);

        handleResourcePickup(ants);
        handleResourceDeposit(ants);
        structures.removeIf(structure -> structure instanceof Resource && ((Resource) structure).getAmount() <= 0);
    }

    public void addResource(Resource resource) {
        structureManager.addStructure(resource);
    }

    public void addResourceNode(ResourceNode resourceNode) {
        structureManager.addStructure(resourceNode);
    }

    public void removeResource(Resource resource) {
        structureManager.removeStructure(resource);
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
        List<Structure> structures = structureManager.getStructures();

        for (Structure structure : structures) {
            GridPoint2 structureGrid = structure.getGridPosition();
            int distance = Math.abs(antGrid.x - structureGrid.x) +
                    Math.abs(antGrid.y - structureGrid.y);
            if (distance > PICKUP_RADIUS) {
                continue;
            }
            // TODO: Breaks Open closed a bit maybe kinda?
            if (structure instanceof Resource) {
                tryPickupResource(ant, (Resource) structure);
            }
            if (structure instanceof ResourceNode) {
                tryHarvestNode(ant, (ResourceNode) structure);
            }
        }
    }

    private void handleResourceDeposit(List<Ant> ants) {
        // TODO: Clean up
        for (Ant ant : ants) {
            Home home = ant.getHome();
            GridPoint2 homeGrid = new GridPoint2((int) home.getPosition().x, (int) home.getPosition().y);
            if (ant.getInventory().isEmpty()) {
                continue;
            }

            GridPoint2 antGrid = getAntGridPosition(ant);
            int distance = Math.abs(antGrid.x - homeGrid.x) +
                    Math.abs(antGrid.y - homeGrid.y);

            if (distance <= DEPOSIT_RADIUS) {
                ant.leaveResources(home);
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

    private boolean tryHarvestNode(Ant ant, ResourceNode node) {
        if (node.isDepleted()) {
            return false;
        }

        int amountToPickup = Math.min(
                node.getCurrentAmount(),
                ant.getInventory().getRemainingCapacity());

        if (amountToPickup > 0 && ant.getInventory().addResource(node.getType(), amountToPickup)) {
            node.harvest(amountToPickup);
            return true;
        }

        return false;
    }
}
