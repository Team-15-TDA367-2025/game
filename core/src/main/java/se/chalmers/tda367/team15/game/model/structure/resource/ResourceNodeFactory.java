package se.chalmers.tda367.team15.game.model.structure.resource;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.world.terrain.StructureSpawn;

public class ResourceNodeFactory {
    public ResourceNodeFactory() {
    }

    public ResourceNode createResourceNode(Vector2 resourcePos, int amount) {
        GridPoint2 resourceGridPoint = new GridPoint2((int) resourcePos.x, (int) resourcePos.y);

        return new ResourceNode(
                resourceGridPoint,
                1,
                ResourceType.FOOD,
                amount,
                1000);
    }

    public ResourceNode createResourceNode(Vector2 resourcePos, StructureSpawn spawn) {
        return createResourceNode(resourcePos, (int) spawn.getProperties().get("amount"));
    }
}
