package se.chalmers.tda367.team15.game.model.structure.resource;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import se.chalmers.tda367.team15.game.model.SimulationProvider;
import se.chalmers.tda367.team15.game.model.managers.StructureManager;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceNode;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;
import se.chalmers.tda367.team15.game.model.world.terrain.StructureSpawn;

public class ResourceNodeFactory {
    private final StructureManager structureManager;

    public ResourceNodeFactory(StructureManager structureManager) {
        this.structureManager = structureManager;
    }

    public void createResourceNode(Vector2 resourcePos, StructureSpawn spawn) {
        GridPoint2 resourceGridPoint = new GridPoint2((int) resourcePos.x, (int) resourcePos.y);

        structureManager.addStructure(
            new ResourceNode(
                resourceGridPoint,
                "node",
                1,
                ResourceType.FOOD,
                (Integer) spawn.getProperties().get("amount"),
                1000));
    }
}
