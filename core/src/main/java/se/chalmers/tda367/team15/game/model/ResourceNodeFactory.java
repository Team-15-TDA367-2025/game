package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceNode;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;

public class ResourceNodeFactory {

    private final SimulationHandler simulationHandler;
    private final Vector2 colonyPos;

    public ResourceNodeFactory(SimulationHandler simulationHandler, Vector2 colonyPos) {
        this.simulationHandler = simulationHandler;
        this.colonyPos = colonyPos;
    }

    public ResourceNode createResourceNode(Vector2 resourcePos) {
        GridPoint2 resourceGridPoint = new GridPoint2((int) resourcePos.x, (int) resourcePos.y);

        double distanceToColony = Math.sqrt((
            Math.pow(resourcePos.x - colonyPos.x, 2) + Math.pow(resourcePos.y - colonyPos.y, 2)));

        int resourceAmount = Math.toIntExact(Math.round(distanceToColony)/10) + 2;

        return new ResourceNode(
            simulationHandler,
            resourceGridPoint,
            "node",
            1,
            ResourceType.FOOD,
            resourceAmount,
            1000);
    }
}
