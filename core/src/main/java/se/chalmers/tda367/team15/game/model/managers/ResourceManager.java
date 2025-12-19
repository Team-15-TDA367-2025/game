package se.chalmers.tda367.team15.game.model.managers;

import java.util.List;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.interfaces.SimulationObserver;
import se.chalmers.tda367.team15.game.model.interfaces.StructureModificationProvider;
import se.chalmers.tda367.team15.game.model.structure.Structure;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceNode;

/**
 * Manages resource interactions using persistent spatial grid.
 * Grid is maintained internally and only modified when resources change.
 */
public class ResourceManager implements SimulationObserver {
    private static final int PICKUP_RADIUS = 2;
    private static final int DEPOSIT_RADIUS = 2;
    private final EntityQuery entityQuery;
    private final StructureModificationProvider structureModificationProvider;

    public ResourceManager(EntityQuery entityQuery, StructureModificationProvider structureMmModificationProvider) {
        this.entityQuery = entityQuery;
        this.structureModificationProvider = structureMmModificationProvider;
    }

    @Override
    public void update(float deltaTime) {
        List<Ant> ants = entityQuery.getEntitiesOfType(Ant.class);

        for (Ant ant : ants) {
            handleHarvest(ant);
            handleDeposit(ant);
        }
    }

    public void addResourceNode(ResourceNode resourceNode) {
        structureModificationProvider.addStructure(resourceNode);
    }

    private void handleHarvest(Ant ant) {
        if (ant.getInventory().isFull()) {
            return;
        }

        GridPoint2 antGrid = getAntGridPosition(ant);
        List<Structure> structures = structureModificationProvider.getStructures();

        for (Structure structure : structures) {
            GridPoint2 structureGrid = structure.getGridPosition();
            int distance = Math.abs(antGrid.x - structureGrid.x) +
                    Math.abs(antGrid.y - structureGrid.y);

            if (distance > PICKUP_RADIUS) {
                continue;
            }

            if (structure instanceof ResourceNode) {
                tryHarvestNode(ant, (ResourceNode) structure);
            }
        }
    }

    private void handleDeposit(Ant ant) {
        if (ant.getInventory().isEmpty()) {
            return;
        }

        Home home = ant.getHome();
        GridPoint2 homeGrid = new GridPoint2((int) home.getPosition().x, (int) home.getPosition().y);

        GridPoint2 antGrid = getAntGridPosition(ant);
        int distance = Math.abs(antGrid.x - homeGrid.x) +
                Math.abs(antGrid.y - homeGrid.y);

        if (distance <= DEPOSIT_RADIUS) {
            ant.leaveResources(home);
        }
    }

    private GridPoint2 getAntGridPosition(Ant ant) {
        Vector2 pos = ant.getPosition();
        return new GridPoint2(Math.round(pos.x), Math.round(pos.y));
    }

    private boolean tryHarvestNode(Ant ant, ResourceNode node) {
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
